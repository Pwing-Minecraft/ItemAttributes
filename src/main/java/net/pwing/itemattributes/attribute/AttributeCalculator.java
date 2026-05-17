package net.pwing.itemattributes.attribute;

import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.attribute.bridge.VanillaAttributeBridge;
import net.pwing.itemattributes.attribute.event.PlayerAttributeApplyEvent;
import net.pwing.itemattributes.modifier.ItemModifier;
import net.pwing.itemattributes.modifier.ModifierType;
import net.pwing.itemattributes.requirement.ItemRequirement;
import net.pwing.itemattributes.requirement.RequirementType;
import net.pwing.itemattributes.util.ExpressionUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class AttributeCalculator {
    private static final String FIRE_TICK_TRACKER_METADATA = "attribute_fire_tick_tracker";
    private static final NamespacedKey FIRE_TICKS_KEY = new NamespacedKey(ItemAttributes.getInstance(), "fire_ticks");

    private static final Map<String, ProjectileTargetHandler> PROJECTILE_TARGET_HANDLERS = Map.of(
            "piercing", (projectile, value) -> {
                if (projectile instanceof AbstractArrow arrow) {
                    arrow.setPierceLevel(Math.max(0, (int) Math.round(value)));
                }
            }
    );

    public static EventAttributeResult calculateEventAttributeResult(Player player, AttributeManager manager, String event, AttributeRequirement<?>... additionalRequirements) {
        return calculateEventAttributeResult(player, manager, event, Map.of(), additionalRequirements);
    }

    public static EventAttributeResult calculateEventAttributeResult(Player player, AttributeManager manager, String event, Map<String, Number> context, AttributeRequirement<?>... additionalRequirements) {
        List<ItemAttribute> attributes = manager.getPlugin().getAttributesConfig().getAttributesByEvent(event);
        if (attributes.isEmpty()) {
            return new EventAttributeResult(0, List.of(), Map.of());
        }

        List<AppliedAttribute> appliedAttributes = new ArrayList<>();
        Map<String, Double> targetedTotals = new HashMap<>();
        double total = 0;
        for (ItemAttribute attribute : attributes) {
            CalculatedAttributeValue calculatedValue = calculateFullAttributeValues(player, manager, attribute, context, additionalRequirements);
            if (calculatedValue.value() == 0 && calculatedValue.targetedValues().isEmpty()) {
                continue;
            }

            PlayerAttributeApplyEvent applyEvent = new PlayerAttributeApplyEvent(player, attribute, calculatedValue.value(), true);
            Bukkit.getPluginManager().callEvent(applyEvent);

            boolean shouldCancelEvent = attribute.getAttribute().isCancelEvent();
            if (applyEvent.isCancelled()) {
                shouldCancelEvent = true;
            }

            appliedAttributes.add(new AppliedAttribute(attribute, calculatedValue.value(), shouldCancelEvent, calculatedValue.targetedValues()));
            total += calculatedValue.value();
            mergeTargetedValues(targetedTotals, calculatedValue.targetedValues());
        }

        return new EventAttributeResult(total, List.copyOf(appliedAttributes), Map.copyOf(targetedTotals));
    }

    public static Number calculateFullAttributeValueForEvent(Player player, AttributeManager manager, String event, AttributeRequirement<?>... additionalRequirements) {
        return calculateFullAttributeValueForEvent(player, manager, event, Map.of(), additionalRequirements);
    }

    public static Number calculateFullAttributeValueForEvent(Player player, AttributeManager manager, String event, Map<String, Number> context, AttributeRequirement<?>... additionalRequirements) {
        return calculateEventAttributeResult(player, manager, event, context, additionalRequirements).total();
    }

    public static Number calculateFullAttributeValue(Player player, AttributeManager manager, ItemAttribute attribute, AttributeRequirement<?>... additionalRequirements) {
        return calculateFullAttributeValue(player, manager, attribute, Map.of(), additionalRequirements);
    }

    public static Number calculateFullAttributeValue(Player player, AttributeManager manager, ItemAttribute attribute, Map<String, Number> context, AttributeRequirement<?>... additionalRequirements) {
        return calculateFullAttributeValues(player, manager, attribute, context, additionalRequirements).value();
    }

    public static CalculatedAttributeValue calculateFullAttributeValues(Player player, AttributeManager manager, ItemAttribute attribute, Map<String, Number> context, AttributeRequirement<?>... additionalRequirements) {
        List<EquipmentSlot> slots = attribute.getSlots()
                .stream()
                .flatMap(group -> group.getSlots().stream())
                .distinct()
                .toList();

        // Now for some funky stuff - if we have items in both hands (main and offhand), we only want
        // the player's main hand to be considered above. As an example, if a player has a sword in their
        // main hand and a sword in their offhand
        boolean shouldCheckOffhand = slots.contains(EquipmentSlot.OFF_HAND) && slots.contains(EquipmentSlot.HAND);

        double value = 0;
        Map<String, Double> targetedValues = new HashMap<>();
        for (EquipmentSlot slot : slots) {
            ItemStack item = player.getInventory().getItem(slot);
            if (item == null || item.getItemMeta() == null) {
                continue;
            }

            if (shouldCheckOffhand) {
                EquipmentSlot oppositeHand = player.getMainHand() == MainHand.RIGHT ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;
                if (oppositeHand == slot && !manager.getPlugin().getPluginConfig().getBypassesOffhandCheck().contains(item.getType())) {
                    continue;
                }
            }

            Optional<Number> valueOptional = manager.attributeValue(item.getItemMeta(), attribute);
            if (valueOptional.isEmpty()) {
                continue;
            }

            Number rawValue = valueOptional.get();
            CalculatedAttributeValue calculatedValue = calculateAttributeValue(player, attribute, rawValue, context, additionalRequirements);
            value += calculatedValue.value();
            mergeTargetedValues(targetedValues, calculatedValue.targetedValues());
        }

        // Now check for modifiers
        AtomicReference<Double> valueDouble = new AtomicReference<>(value);

        ItemModifier.applyModifiers(ModifierType.ATTRIBUTE, player, modifier -> {
            if (modifier.getAttribute() == attribute) {
                double modifierValue = modifier.getValue().doubleValue();
                CalculatedAttributeValue calculatedValue = calculateAttributeValue(player, attribute, modifierValue, context, additionalRequirements);
                valueDouble.set(valueDouble.get() + calculatedValue.value());
                mergeTargetedValues(targetedValues, calculatedValue.targetedValues());
            }
        });

        return new CalculatedAttributeValue(valueDouble.get(), Map.copyOf(targetedValues));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static CalculatedAttributeValue calculateAttributeValue(Player player, ItemAttribute attribute, Number rawValue, Map<String, Number> context, AttributeRequirement<?>... additionalRequirements) {
        List<ItemRequirement<ItemAttribute>> requirements = attribute.getRequirements(RequirementType.ATTRIBUTE);
        for (ItemRequirement<ItemAttribute> requirement : requirements) {
            if (!requirement.hasRequirement(attribute, player)) {
                return new CalculatedAttributeValue(0, Map.of());
            }
        }

        AttributeApplicator calculationApplicator = attribute.getAttribute().getSourceOrSelf();

        for (AttributeRequirement requirement : additionalRequirements) {
            List<ItemRequirement> itemRequirements = attribute.getRequirements(requirement.type());
            if (itemRequirements.isEmpty()) {
                continue;
            }

            for (ItemRequirement itemRequirement : itemRequirements) {
                if (!itemRequirement.hasRequirement(requirement.value(), player)) {
                    return new CalculatedAttributeValue(0, Map.of());
                }
            }
        }

        if (calculationApplicator.getOperation() == AttributeOperation.PERCENTAGE || calculationApplicator.getOperation() == AttributeOperation.TOTAL_PERCENTAGE) {
            rawValue = rawValue.doubleValue() / 100.0D;
        }

        Map<String, Double> targetedValues = new HashMap<>();
        for (Map.Entry<String, String> entry : calculationApplicator.getTargetedModifierExpressions().entrySet()) {
            targetedValues.put(entry.getKey(), evaluateExpression(entry.getValue(), rawValue, context));
        }

        double value = rawValue.doubleValue();
        String modifierExpression = calculationApplicator.getModifierExpression();
        if (modifierExpression != null) {
            value = evaluateExpression(modifierExpression, rawValue, context);
        } else if (!targetedValues.isEmpty()) {
            value = 0.0D;
        }

        return new CalculatedAttributeValue(value, Map.copyOf(targetedValues));
    }

    public static String computeOperators(String modifierExpression, Number rawValue) {
        return computeOperators(modifierExpression, rawValue, Map.of());
    }

    public static String computeOperators(String modifierExpression, Number rawValue, Map<String, Number> context) {
        String parsedExpression = modifierExpression.replace("%value%", String.valueOf(rawValue)).replace("%operator%", rawValue.doubleValue() >= 0 ? "+" : "");
        for (Map.Entry<String, Number> entry : context.entrySet()) {
            parsedExpression = parsedExpression.replace("%" + entry.getKey() + "%", String.valueOf(entry.getValue()));
        }

        return parsedExpression.replaceAll("%[a-zA-Z0-9_]+%", "0");
    }

    public static Component computeOperators(Component modifier, Number rawValue) {
        return modifier.replaceText(builder -> builder.matchLiteral("%value%").replacement(String.valueOf(rawValue)))
                .replaceText(builder -> builder.matchLiteral("%operator%").replacement(rawValue.doubleValue() >= 0 ? "+" : ""));
    }

    private static double evaluateExpression(String modifierExpression, Number rawValue, Map<String, Number> context) {
        String parsedExpression = computeOperators(modifierExpression, rawValue, context);
        return ExpressionUtils.createExpression(parsedExpression).evaluate();
    }

    private static void mergeTargetedValues(Map<String, Double> totals, Map<String, Double> values) {
        for (Map.Entry<String, Double> entry : values.entrySet()) {
            totals.merge(entry.getKey(), entry.getValue(), Double::sum);
        }
    }

    public static double calculateAttributeValue(double baseValue, double modifierValue, AttributeOperation operation) {
        return switch (operation) {
            case ADD -> baseValue + modifierValue;
            case PERCENTAGE, TOTAL_PERCENTAGE -> baseValue * (1 + modifierValue); // For our case, this will be the same
        };
    }

    public static List<RedirectApplication> applyRedirectAttributes(AttributeManager manager, Player player, Entity targetEntity, String eventName, Map<String, Number> context, AttributeRequirement<?>... additionalRequirements) {
        List<RedirectApplication> applications = new ArrayList<>();
        for (ItemAttribute attribute : manager.getPlugin().getAttributesConfig().getAttributesByEvent(eventName)) {
            if (attribute.getAttribute().getType() != AttributeBridgeType.REDIRECT) {
                continue;
            }

            Number redirectedValue = calculateFullAttributeValue(player, manager, attribute, context, additionalRequirements);
            if (redirectedValue.doubleValue() == 0) {
                continue;
            }

            if (attribute.getAttribute().getDestinationEntity() == RedirectDestination.TARGET) {
                boolean activated = applyRedirectToTarget(attribute, targetEntity, redirectedValue);
                applications.add(new RedirectApplication(attribute, redirectedValue.doubleValue(), activated));
                continue;
            }

            boolean activated = applyRedirectToSelf(attribute, player, redirectedValue);
            applications.add(new RedirectApplication(attribute, redirectedValue.doubleValue(), activated));
        }

        return applications;
    }

    public static boolean applyRedirectToTarget(ItemAttribute attribute, Entity targetEntity, Number redirectedValue) {
        if (!(targetEntity instanceof LivingEntity livingTarget)) {
            return false;
        }

        AttributeApplicator destination = attribute.getAttribute().getResolvedDestination();
        if (destination == null) {
            return false;
        }

        if (livingTarget instanceof Player targetPlayer) {
            return applyRedirectToSelf(attribute, targetPlayer, redirectedValue);
        }

        if (destination.getType() != AttributeBridgeType.VANILLA) {
            return false;
        }

        VanillaAttributeBridge.PseudoAttributeApplyResult result = VanillaAttributeBridge.applyPseudoAttributeWithResult(livingTarget, destination, redirectedValue);
        return result.applied() && result.activated();
    }

    public static boolean applyRedirectToSelf(ItemAttribute attribute, Player player, Number redirectedValue) {
        AttributeApplicator destination = attribute.getAttribute().getResolvedDestination();
        if (destination == null) {
            return false;
        }

        if (destination.getType() == AttributeBridgeType.VANILLA) {
            VanillaAttributeBridge.PseudoAttributeApplyResult result = VanillaAttributeBridge.applyPseudoAttributeWithResult(player, destination, redirectedValue);
            if (result.applied()) {
                return result.activated();
            }
        }

        attribute.getAttribute().apply(player, redirectedValue);
        return true;
    }

    public static boolean applySuccessEventsToDamageEvent(AttributeManager manager, Player sourcePlayer, EntityDamageEvent event, AttributeRequirement<?> entityRequirement, Set<String> successEvents) {
        for (String successEvent : successEvents) {
            AttributeCalculator.EventAttributeResult result = AttributeCalculator.calculateEventAttributeResult(sourcePlayer, manager, successEvent, Map.of("damage", event.getFinalDamage()), entityRequirement);
            if (!applyNonRedirectDamageBonuses(event, result)) {
                return false;
            }
        }

        return true;
    }

    public static void applyProjectileLaunchTargets(Projectile projectile, AttributeCalculator.EventAttributeResult result) {
        for (Map.Entry<String, Double> entry : result.targetedTotals().entrySet()) {
            ProjectileTargetHandler handler = PROJECTILE_TARGET_HANDLERS.get(entry.getKey());
            if (handler == null) {
                continue;
            }

            handler.apply(projectile, entry.getValue());
        }
    }

    public static boolean applyNonRedirectDamageBonuses(EntityDamageEvent event, AttributeCalculator.EventAttributeResult result) {
        for (AttributeCalculator.AppliedAttribute applied : result.appliedAttributes()) {
            if (applied.attribute().getAttribute().getType() == AttributeBridgeType.REDIRECT) {
                continue;
            }

            double afterDamage = event.getDamage() + applied.value();
            if (afterDamage < 0 && applied.shouldCancelEvent()) {
                event.setCancelled(true);
                return false;
            }

            event.setDamage(Math.max(0, afterDamage));
        }

        return true;
    }

    public static double getNonRedirectTotal(AttributeCalculator.EventAttributeResult result) {
        double sum = 0.0;
        for (AttributeCalculator.AppliedAttribute applied : result.appliedAttributes()) {
            if (applied.attribute().getAttribute().getType() != AttributeBridgeType.REDIRECT) {
                double value = applied.value();
                sum += value;
            }
        }
        return sum;
    }

    public static boolean shouldNonRedirectCancel(AttributeCalculator.EventAttributeResult result) {
        for (AttributeCalculator.AppliedAttribute applied : result.appliedAttributes()) {
            if (applied.attribute().getAttribute().getType() != AttributeBridgeType.REDIRECT) {
                if (applied.shouldCancelEvent()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void applyRedirectSuccessEventBonuses(AttributeManager manager, Player player, EntityDamageByEntityEvent event, Entity targetEntity, AttributeRequirement<?> entityRequirement, List<RedirectApplication> redirectApplications) {
        Set<String> fireTickSuccessEvents = new LinkedHashSet<>();
        Set<String> triggeredSuccessEvents = new LinkedHashSet<>();
        for (RedirectApplication application : redirectApplications) {
            if (!application.activated() || application.successEvent() == null) {
                continue;
            }

            if (isFireTickDeferredRedirect(application)) {
                fireTickSuccessEvents.add(application.successEvent());
                continue;
            }

            triggeredSuccessEvents.add(application.successEvent());
        }

        if (!fireTickSuccessEvents.isEmpty()) {
            trackFireTickSuccessEvents(player, targetEntity, fireTickSuccessEvents);
        }

        if (!triggeredSuccessEvents.isEmpty()) {
            applySuccessEventsToDamageEvent(manager, player, event, entityRequirement, triggeredSuccessEvents);
        }
    }

    public static boolean isFireTickDeferredRedirect(RedirectApplication application) {
        if (application.attribute().getAttribute().getDestinationEntity() != RedirectDestination.TARGET) {
            return false;
        }

        AttributeApplicator destination = application.attribute().getAttribute().getResolvedDestination();
        if (destination == null || destination.getType() != AttributeBridgeType.VANILLA) {
            return false;
        }

        return FIRE_TICKS_KEY.equals(destination.getKey());
    }

    public static void applyTrackedFireTickSuccessEvents(AttributeManager manager, EntityDamageEvent event) {
        FireTickTracker tracker = getFireTickTracker(event.getEntity());
        if (tracker == null) {
            return;
        }

        Player sourcePlayer = Bukkit.getPlayer(tracker.sourcePlayerId());
        if (sourcePlayer == null) {
            clearFireTickTracker(event.getEntity());
            return;
        }

        AttributeRequirement<?> entityRequirement = new AttributeRequirement<>(RequirementType.ENTITY, event.getEntity().getType());
        if (!applySuccessEventsToDamageEvent(manager, sourcePlayer, event, entityRequirement, tracker.successEvents())) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity livingEntity) || livingEntity.getFireTicks() <= 0) {
            clearFireTickTracker(event.getEntity());
        }
    }

    public static void trackFireTickSuccessEvents(Player sourcePlayer, Entity targetEntity, Set<String> successEvents) {
        if (!(targetEntity instanceof LivingEntity livingEntity)) {
            return;
        }

        FireTickTracker existingTracker = getFireTickTracker(livingEntity);
        Set<String> mergedEvents = new LinkedHashSet<>(successEvents);
        if (existingTracker != null && existingTracker.sourcePlayerId().equals(sourcePlayer.getUniqueId())) {
            mergedEvents.addAll(existingTracker.successEvents());
        }

        FireTickTracker tracker = new FireTickTracker(sourcePlayer.getUniqueId(), Set.copyOf(mergedEvents));
        livingEntity.setMetadata(FIRE_TICK_TRACKER_METADATA, new FixedMetadataValue(ItemAttributes.getInstance(), tracker));
    }

    public static FireTickTracker getFireTickTracker(Entity entity) {
        List<MetadataValue> metadataValues = entity.getMetadata(FIRE_TICK_TRACKER_METADATA);
        for (MetadataValue metadataValue : metadataValues) {
            if (metadataValue.getOwningPlugin() != ItemAttributes.getInstance()) {
                continue;
            }

            Object value = metadataValue.value();
            if (value instanceof FireTickTracker tracker) {
                return tracker;
            }
        }

        return null;
    }

    public static void clearFireTickTracker(Entity entity) {
        entity.removeMetadata(FIRE_TICK_TRACKER_METADATA, ItemAttributes.getInstance());
    }

    public record RedirectApplication(ItemAttribute attribute, double value, boolean activated) {
        public String successEvent() {
            return this.attribute.getAttribute().getSuccessEvent();
        }
    }

    public record FireTickTracker(UUID sourcePlayerId, Set<String> successEvents) {
    }

    @FunctionalInterface
    public interface ProjectileTargetHandler {
        void apply(Projectile projectile, double value);
    }

    public record AppliedAttribute(ItemAttribute attribute, double value, boolean shouldCancelEvent, Map<String, Double> targetedValues) {
        public double targetedValue(String key) {
            return this.targetedValues.getOrDefault(key, 0.0D);
        }
    }

    public record EventAttributeResult(double total, List<AppliedAttribute> appliedAttributes, Map<String, Double> targetedTotals) {
        public double targetedTotal(String key) {
            return this.targetedTotals.getOrDefault(key, 0.0D);
        }
    }

    public record CalculatedAttributeValue(double value, Map<String, Double> targetedValues) {
    }
}

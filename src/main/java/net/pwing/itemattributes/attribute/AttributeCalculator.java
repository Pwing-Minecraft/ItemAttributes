package net.pwing.itemattributes.attribute;

import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.modifier.ItemModifier;
import net.pwing.itemattributes.modifier.ModifierType;
import net.pwing.itemattributes.requirement.ItemRequirement;
import net.pwing.itemattributes.requirement.RequirementType;
import net.pwing.itemattributes.util.ExpressionUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class AttributeCalculator {

    public static Number calculateFullAttributeValueForEvent(Player player, AttributeManager manager, String event, AttributeRequirement<?>... additionalRequirements) {
        List<ItemAttribute> attributes = manager.getPlugin().getAttributesConfig().getAttributesByEvent(event);
        if (attributes.isEmpty()) {
            return 0;
        }

        Number value = 0;
        for (ItemAttribute attribute : attributes) {
            value = value.doubleValue() + calculateFullAttributeValue(player, manager, attribute, additionalRequirements).doubleValue();
        }

        return value;
    }

    public static <T> Number calculateFullAttributeValue(Player player, AttributeManager manager, ItemAttribute attribute, AttributeRequirement<?>... additionalRequirements) {
        List<EquipmentSlot> slots = attribute.getSlots()
                .stream()
                .flatMap(group -> group.getSlots().stream())
                .distinct()
                .toList();

        // Now for some funky stuff - if we have items in both hands (main and offhand), we only want
        // the player's main hand to be considered above. As an example, if a player has a sword in their
        // main hand and a sword in their offhand
        boolean shouldCheckOffhand = slots.contains(EquipmentSlot.OFF_HAND) && slots.contains(EquipmentSlot.HAND);

        Number value = 0;
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
            value = value.doubleValue() + calculateAttributeValue(player, attribute, rawValue, additionalRequirements).doubleValue();
        }

        // Now check for modifiers
        AtomicReference<Double> valueDouble = new AtomicReference<>(value.doubleValue());

        ItemModifier.applyModifiers(ModifierType.ATTRIBUTE, player, modifier -> {
            if (modifier.getAttribute() == attribute) {
                double modifierValue = modifier.getValue().doubleValue();
                valueDouble.set(valueDouble.get() + calculateAttributeValue(player, attribute, modifierValue, additionalRequirements).doubleValue());
            }
        });

        return valueDouble.get();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Number calculateAttributeValue(Player player, ItemAttribute attribute, Number rawValue, AttributeRequirement<?>... additionalRequirements) {
        List<ItemRequirement<ItemAttribute>> requirements = attribute.getRequirements(RequirementType.ATTRIBUTE);
        for (ItemRequirement<ItemAttribute> requirement : requirements) {
            if (!requirement.hasRequirement(attribute, player)) {
                return 0;
            }
        }

        for (AttributeRequirement requirement : additionalRequirements) {
            List<ItemRequirement> itemRequirements = attribute.getRequirements(requirement.type());
            if (itemRequirements.isEmpty()) {
                continue;
            }

            for (ItemRequirement itemRequirement : itemRequirements) {
                if (!itemRequirement.hasRequirement(requirement.value(), player)) {
                    return 0;
                }
            }
        }

        if (attribute.getAttribute().getOperation() == AttributeOperation.PERCENTAGE || attribute.getAttribute().getOperation() == AttributeOperation.TOTAL_PERCENTAGE) {
            rawValue = rawValue.doubleValue() / 100.0D;
        }

        String modifierExpression = attribute.getAttribute().getModifierExpression();
        if (modifierExpression != null) {
            String parsedExpression = computeOperators(modifierExpression, rawValue);
            rawValue = ExpressionUtils.createExpression(parsedExpression).evaluate();
        }

        return rawValue;
    }

    public static String computeOperators(String modifierExpression, Number rawValue) {
        return modifierExpression.replace("%value%", String.valueOf(rawValue)).replace("%operator%", rawValue.doubleValue() >= 0 ? "+" : "");
    }

    public static Component computeOperators(Component modifier, Number rawValue) {
        return modifier.replaceText(builder -> builder.matchLiteral("%value%").replacement(String.valueOf(rawValue)))
                .replaceText(builder -> builder.matchLiteral("%operator%").replacement(rawValue.doubleValue() >= 0 ? "+" : ""));
    }

    public static double calculateAttributeValue(double baseValue, double modifierValue, AttributeOperation operation) {
        return switch (operation) {
            case ADD -> baseValue + modifierValue;
            case PERCENTAGE, TOTAL_PERCENTAGE -> baseValue * (1 + modifierValue); // For our case, this will be the same
        };
    }
}

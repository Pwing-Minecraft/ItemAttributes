package net.pwing.itemattributes.feature.attribute;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.events.SpellApplyDamageEvent;
import com.nisovin.magicspells.events.SpellCastEvent;
import com.nisovin.magicspells.mana.ManaBar;
import com.nisovin.magicspells.mana.ManaRank;
import com.nisovin.magicspells.mana.ManaSystem;
import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.attribute.AttributeApplicator;
import net.pwing.itemattributes.attribute.AttributeCalculator;
import net.pwing.itemattributes.attribute.AttributeManager;
import net.pwing.itemattributes.attribute.AttributeRequirement;
import net.pwing.itemattributes.feature.PluginFeature;
import net.pwing.itemattributes.requirement.RequirementType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;

public class MagicSpellsAttributeFeature extends PluginFeature<AttributesFeature> implements AttributesFeature, Listener {
    private static final Method GET_MANA_BAR_METHOD;

    static {
        try {
            GET_MANA_BAR_METHOD = ManaSystem.class.getDeclaredMethod("getManaBar", Player.class);
            GET_MANA_BAR_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to access getManaBar method", e);
        }
    }

    public MagicSpellsAttributeFeature() {
        super("MagicSpells");

        Bukkit.getPluginManager().registerEvents(this, ItemAttributes.getInstance());
    }

    @Override
    public Number getValue(Player player, NamespacedKey attributeKey) {
        MagicSpellsAttribute attribute = getAttribute(attributeKey);
        if (attribute == null) {
            return null;
        }

        return attribute.getValue(player);
    }

    @Override
    public void apply(Player player, AttributeApplicator applicator, Number value) {
        MagicSpellsAttribute attribute = getAttribute(applicator.getKey());
        if (attribute == null) {
            return;
        }

        attribute.apply(player, (int) AttributeCalculator.calculateAttributeValue(attribute.getBaseValue(player), value.doubleValue(), applicator.getOperation()));

        // Refresh the mana when we update it
        MagicSpells.getManaHandler().showMana(player);
    }

    @Override
    public void reset(Player player, NamespacedKey attributeKey) {
        MagicSpellsAttribute attribute = getAttribute(attributeKey);
        if (attribute == null) {
            return;
        }

        attribute.apply(player, attribute.getBaseValue(player));

        // Refresh the mana when we update it
        MagicSpells.getManaHandler().showMana(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSpellCast(SpellCastEvent event) {
        if (event.getCaster() instanceof Player player) {
            AttributeCalculator.EventAttributeResult result = AttributeCalculator.calculateEventAttributeResult(player, ItemAttributes.getInstance().getAttributeManager(), "spell_cast", Map.of("cooldown", event.getCooldown()));
            float oldCooldown = event.getCooldown();
            float cooldownAdjustment = (float) AttributeCalculator.getNonRedirectTotal(result);
            if (cooldownAdjustment != 0) {
                float rawValue = event.getCooldown() + cooldownAdjustment;
                event.setCooldown(Math.max(0, rawValue));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSpellApplyDamage(SpellApplyDamageEvent event) {
        AttributeManager manager = ItemAttributes.getInstance().getAttributeManager();
        if (event.getCaster() instanceof Player player) {
            AttributeRequirement<?> entityRequirement = new AttributeRequirement<>(RequirementType.ENTITY, event.getTarget().getType());
            AttributeCalculator.EventAttributeResult result = AttributeCalculator.calculateEventAttributeResult(player, manager, "entity_spell_damage", Map.of("damage", event.getFinalDamage()), entityRequirement);
            double nonRedirectTotal = AttributeCalculator.getNonRedirectTotal(result);
            if (nonRedirectTotal != 0) {
                applySpellDamageModifier(event, nonRedirectTotal, AttributeCalculator.shouldNonRedirectCancel(result));
            }

            List<AttributeCalculator.RedirectApplication> redirectApplications = AttributeCalculator.applyRedirectAttributes(manager, player, event.getTarget(), "entity_spell_damage", Map.of("damage", event.getFinalDamage()), entityRequirement);
            applyRedirectSuccessEventBonuses(manager, player, event, event.getTarget(), entityRequirement, redirectApplications);
        }

        if (event.getTarget() instanceof Player player && event.getCaster() != null) {
            AttributeRequirement<?> entityRequirement = new AttributeRequirement<>(RequirementType.ENTITY, event.getCaster().getType());
            AttributeCalculator.EventAttributeResult result = AttributeCalculator.calculateEventAttributeResult(player, manager, "entity_take_spell_damage", Map.of("damage", event.getFinalDamage()), entityRequirement);
            double nonRedirectTotal = AttributeCalculator.getNonRedirectTotal(result);
            if (nonRedirectTotal != 0) {
                applySpellDamageModifier(event, nonRedirectTotal, AttributeCalculator.shouldNonRedirectCancel(result));
            }

            AttributeCalculator.applyRedirectAttributes(manager, player, event.getCaster(), "entity_take_spell_damage", Map.of("damage", event.getFinalDamage()), entityRequirement);
        }
    }

    private static ManaRank getManaRank(Player player) {
        if (!(MagicSpells.getManaHandler() instanceof ManaSystem system)) {
            return null;
        }

        try {
            ManaBar bar = (ManaBar) GET_MANA_BAR_METHOD.invoke(system, player);
            return bar.getManaRank();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    private static MagicSpellsAttribute getAttribute(NamespacedKey key) {
        try {
            return MagicSpellsAttribute.valueOf(key.getKey().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static void applyRedirectSuccessEventBonuses(AttributeManager manager, Player player, SpellApplyDamageEvent event, Entity targetEntity, AttributeRequirement<?> entityRequirement, List<AttributeCalculator.RedirectApplication> redirectApplications) {
        Set<String> fireTickSuccessEvents = new LinkedHashSet<>();
        Set<String> triggeredSuccessEvents = new LinkedHashSet<>();
        for (AttributeCalculator.RedirectApplication application : redirectApplications) {
            if (!application.activated() || application.successEvent() == null) {
                continue;
            }

            if (AttributeCalculator.isFireTickDeferredRedirect(application)) {
                fireTickSuccessEvents.add(application.successEvent());
                continue;
            }

            triggeredSuccessEvents.add(application.successEvent());
        }

        if (!fireTickSuccessEvents.isEmpty()) {
            AttributeCalculator.trackFireTickSuccessEvents(player, targetEntity, fireTickSuccessEvents);
        }

        if (!triggeredSuccessEvents.isEmpty()) {
            applySuccessEventsToSpellDamageEvent(manager, player, event, entityRequirement, triggeredSuccessEvents);
        }
    }

    private static boolean applySuccessEventsToSpellDamageEvent(AttributeManager manager, Player sourcePlayer, SpellApplyDamageEvent event, AttributeRequirement<?> entityRequirement, Set<String> successEvents) {
        for (String successEvent : successEvents) {
            AttributeCalculator.EventAttributeResult result = AttributeCalculator.calculateEventAttributeResult(sourcePlayer, manager, successEvent, Map.of("damage", event.getFinalDamage()), entityRequirement);
            if (!applyNonRedirectSpellDamageBonuses(event, result)) {
                return false;
            }
        }

        return true;
    }

    private static boolean applyNonRedirectSpellDamageBonuses(SpellApplyDamageEvent event, AttributeCalculator.EventAttributeResult result) {
        double nonRedirectTotal = AttributeCalculator.getNonRedirectTotal(result);
        if (nonRedirectTotal == 0) {
            return true;
        }

        return applySpellDamageModifier(event, nonRedirectTotal, AttributeCalculator.shouldNonRedirectCancel(result));
    }

    private static boolean applySpellDamageModifier(SpellApplyDamageEvent event, double modifier, boolean shouldCancelWhenNegative) {
        // Mirror mutable EntityDamageEvent semantics by building from the event's current damage state.
        double rawValue = event.getFinalDamage() + modifier;
        if (rawValue < 0 && shouldCancelWhenNegative) {
            event.setFlatModifier(-(event.getDamage() * event.getDamageModifier())); // Sets the final value to zero
            return false;
        }

        event.applyFlatDamageModifier(modifier);
        return true;
    }

    public enum MagicSpellsAttribute {
        MANA_CAPACITY("mana_capacity", player -> getManaRank(player).getMaxMana(), MagicSpells.getManaHandler()::getMaxMana, MagicSpells.getManaHandler()::setMaxMana),
        MANA_REGEN("mana_regen", player -> getManaRank(player).getRegenAmount(), MagicSpells.getManaHandler()::getRegenAmount, MagicSpells.getManaHandler()::setRegenAmount);

        private final String key;
        private final ToIntFunction<Player> baseAttributeGetter;
        private final ToIntFunction<Player> attributeGetter;
        private final ObjIntConsumer<Player> attributeApplier;

        MagicSpellsAttribute(String key, ToIntFunction<Player> baseAttributeGetter, ToIntFunction<Player> attributeGetter, ObjIntConsumer<Player> attributeApplier) {
            this.key = key;
            this.baseAttributeGetter = baseAttributeGetter;
            this.attributeGetter = attributeGetter;
            this.attributeApplier = attributeApplier;
        }

        public String getKey() {
            return this.key;
        }

        public int getBaseValue(Player player) {
            return this.baseAttributeGetter.applyAsInt(player);
        }

        public int getValue(Player player) {
            return this.attributeGetter.applyAsInt(player);
        }

        public void apply(Player player, int value) {
            this.attributeApplier.accept(player, value);
        }
    }
}

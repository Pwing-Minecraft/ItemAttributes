package net.pwing.itemattributes.feature.attribute;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.aurelium.auraskills.api.util.AuraSkillsModifier;
import net.pwing.itemattributes.attribute.AttributeApplicator;
import net.pwing.itemattributes.attribute.AttributeCalculator;
import net.pwing.itemattributes.feature.PluginFeature;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ToDoubleFunction;

public class AuraSkillsAttributeFeature extends PluginFeature<AttributesFeature> implements AttributesFeature {

    public AuraSkillsAttributeFeature() {
        super("AuraSkills");

    }

    @Override
    public Number getValue(Player player, NamespacedKey attributeKey) {
        AuraSkillsAttribute attribute = getAttribute(attributeKey);
        if (attribute == null) {
            return null;
        }

        SkillsUser user = AuraSkillsApi.get().getUser(player.getUniqueId());
        return attribute.getValue(user);
    }

    @Override
    public void apply(Player player, AttributeApplicator applicator, Number value) {
        AuraSkillsAttribute attribute = getAttribute(applicator.getKey());
        if (attribute == null) {
            return;
        }

        SkillsUser user = AuraSkillsApi.get().getUser(player.getUniqueId());
        attribute.apply(user, (int) AttributeCalculator.calculateAttributeValue(attribute.getBaseValue(user), value.doubleValue(), applicator.getOperation()));
    }

    @Override
    public void reset(Player player, NamespacedKey attributeKey) {
        AuraSkillsAttribute attribute = getAttribute(attributeKey);
        if (attribute == null) {
            return;
        }

        SkillsUser user = AuraSkillsApi.get().getUser(player.getUniqueId());
        attribute.apply(user, attribute.getBaseValue(user));
    }

    @Nullable
    private static AuraSkillsAttribute getAttribute(NamespacedKey key) {
        try {
            return AuraSkillsAttribute.valueOf(key.getKey().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public enum AuraSkillsAttribute {
        MANA_CAPACITY("mana_capacity", user -> Traits.MAX_MANA.optionDouble("base"), SkillsUser::getMaxMana, (user, value) -> {
            TraitModifier modifier = user.getTraitModifier("mana_capacity");
            if (modifier != null) {
                user.removeTraitModifier("mana_capacity");
            }

            modifier = new TraitModifier("mana_capacity", Traits.MAX_MANA, value, AuraSkillsModifier.Operation.ADD);
            user.addTraitModifier(modifier);
        }),
        MANA_REGEN("mana_regen", user -> Traits.MANA_REGEN.optionDouble("base"), user -> user.getEffectiveTraitLevel(Traits.MANA_REGEN), (user, value) -> {
            TraitModifier modifier = user.getTraitModifier("mana_regen");
            if (modifier != null) {
                user.removeTraitModifier("mana_regen");
            }

            modifier = new TraitModifier("mana_regen", Traits.MANA_REGEN, value, AuraSkillsModifier.Operation.ADD);
            user.addTraitModifier(modifier);
        });

        private final String key;
        private final ToDoubleFunction<SkillsUser> baseAttributeGetter;
        private final ToDoubleFunction<SkillsUser> attributeGetter;
        private final ObjDoubleConsumer<SkillsUser> attributeApplier;

        AuraSkillsAttribute(String key, ToDoubleFunction<SkillsUser> baseAttributeGetter, ToDoubleFunction<SkillsUser> attributeGetter, ObjDoubleConsumer<SkillsUser> attributeApplier) {
            this.key = key;
            this.baseAttributeGetter = baseAttributeGetter;
            this.attributeGetter = attributeGetter;
            this.attributeApplier = attributeApplier;
        }

        public String getKey() {
            return this.key;
        }

        public double getBaseValue(SkillsUser user) {
            return this.baseAttributeGetter.applyAsDouble(user);
        }

        public double getValue(SkillsUser user) {
            return this.attributeGetter.applyAsDouble(user);
        }

        public void apply(SkillsUser user, double value) {
            this.attributeApplier.accept(user, value);
        }
    }
}

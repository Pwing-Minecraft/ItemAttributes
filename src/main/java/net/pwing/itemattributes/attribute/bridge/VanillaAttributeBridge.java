package net.pwing.itemattributes.attribute.bridge;

import net.pwing.itemattributes.attribute.AttributeApplicator;
import net.pwing.itemattributes.attribute.AttributeOperation;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.util.Map;

public class VanillaAttributeBridge implements AttributeBridge {
    public static final VanillaAttributeBridge INSTANCE = new VanillaAttributeBridge();

    private static final Map<NamespacedKey, PseudoVanillaAttribute> PSEUDO_ATTRIBUTES = Map.of(
            NamespacedKey.minecraft("health"), new BoundedPseudoVanillaAttribute() {

                @Override
                public double currentValue(LivingEntity entity) {
                    return entity.getHealth();
                }

                @Override
                protected double referenceValue(LivingEntity entity) {
                    return this.maxValue(entity);
                }

                @Override
                protected double maxValue(LivingEntity entity) {
                    AttributeInstance maxHealthAttribute = entity.getAttribute(Attribute.MAX_HEALTH);
                    return maxHealthAttribute == null ? entity.getHealth() : maxHealthAttribute.getValue();
                }

                @Override
                protected void setValue(LivingEntity entity, double value) {
                    entity.setHealth(value);
                }
            },
            NamespacedKey.minecraft("fire_ticks"), new BoundedPseudoVanillaAttribute() {

                @Override
                public double currentValue(LivingEntity entity) {
                    return entity.getFireTicks();
                }

                @Override
                protected double referenceValue(LivingEntity entity) {
                    return Math.max(20.0D, entity.getMaxFireTicks());
                }

                @Override
                protected double maxValue(LivingEntity entity) {
                    return Integer.MAX_VALUE;
                }

                @Override
                protected void setValue(LivingEntity entity, double value) {
                    entity.setFireTicks((int) Math.round(value));
                }

                @Override
                public boolean didActivate(double beforeValue, double afterValue) {
                    return beforeValue <= 0.0D && afterValue > 0.0D;
                }
            }
    );

    private VanillaAttributeBridge() {
    }

    @Override
    public void apply(Player player, AttributeApplicator applicator, Number value) {
        if (applyPseudoAttribute(player, applicator, value)) {
            return;
        }

        // Reset when applying modifiers
        this.reset(player, applicator);

        NamespacedKey key = applicator.getKey();
        Attribute attribute = Registry.ATTRIBUTE.get(key);
        if (attribute == null) {
            return; // Caught when validating attributes
        }

        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }

        if (applicator.getOperation() == AttributeOperation.ADD) {
            instance.addModifier(new AttributeModifier(key, value.doubleValue(), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));
        } else if (applicator.getOperation() == AttributeOperation.PERCENTAGE) {
            instance.addModifier(new AttributeModifier(key, value.doubleValue(), AttributeModifier.Operation.ADD_SCALAR, EquipmentSlotGroup.ANY));
        } else {
            instance.addModifier(new AttributeModifier(key, value.doubleValue(), AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ANY));
        }
    }

    @Override
    public void reset(Player player, AttributeApplicator applicator) {
        NamespacedKey key = applicator.getKey();
        PseudoVanillaAttribute pseudoAttribute = pseudoAttribute(key);
        if (pseudoAttribute != null) {
            pseudoAttribute.reset(player, applicator);
            return;
        }

        Attribute attribute = Registry.ATTRIBUTE.get(key);
        if (attribute == null) {
            return; // Caught when validating attributes
        }

        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }

        AttributeModifier appliedModifier = null;
        for (AttributeModifier modifier : instance.getModifiers()) {
            if (modifier.getKey().equals(key)) {
                appliedModifier = modifier;
                break;
            }
        }

        if (appliedModifier != null) {
            instance.removeModifier(appliedModifier);
        }
    }

    @Override
    public Number getValue(Player player, NamespacedKey key) {
        PseudoVanillaAttribute pseudoAttribute = pseudoAttribute(key);
        if (pseudoAttribute != null) {
            return pseudoAttribute.getValue(player);
        }

        Attribute attribute = Registry.ATTRIBUTE.get(key);
        if (attribute == null) {
            return 0;
        }

        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return 0;
        }

        return instance.getValue();
    }

    public static boolean applyPseudoAttribute(LivingEntity entity, AttributeApplicator applicator, Number value) {
        return applyPseudoAttributeWithResult(entity, applicator, value).applied();
    }

    public static PseudoAttributeApplyResult applyPseudoAttributeWithResult(LivingEntity entity, AttributeApplicator applicator, Number value) {
        PseudoVanillaAttribute pseudoAttribute = pseudoAttribute(applicator.getKey());
        if (pseudoAttribute == null) {
            return PseudoAttributeApplyResult.NOT_APPLIED;
        }

        double beforeValue = pseudoAttribute.currentValue(entity);
        pseudoAttribute.apply(entity, applicator, value);
        double afterValue = pseudoAttribute.currentValue(entity);
        boolean activated = pseudoAttribute.didActivate(beforeValue, afterValue);
        return new PseudoAttributeApplyResult(true, activated);
    }

    private static PseudoVanillaAttribute pseudoAttribute(NamespacedKey key) {
        return PSEUDO_ATTRIBUTES.get(key);
    }

    private interface PseudoVanillaAttribute {
        void apply(LivingEntity entity, AttributeApplicator applicator, Number value);

        void reset(LivingEntity entity, AttributeApplicator applicator);

        Number getValue(LivingEntity entity);

        double currentValue(LivingEntity entity);

        default boolean didActivate(double beforeValue, double afterValue) {
            return afterValue > beforeValue;
        }
    }

    private abstract static class BoundedPseudoVanillaAttribute implements PseudoVanillaAttribute {

        @Override
        public void apply(LivingEntity entity, AttributeApplicator applicator, Number value) {
            double modifierValue = value.doubleValue();
            double newValue = switch (applicator.getOperation()) {
                case ADD -> this.currentValue(entity) + modifierValue;
                case PERCENTAGE, TOTAL_PERCENTAGE -> this.currentValue(entity) + (this.referenceValue(entity) * modifierValue);
            };

            this.setValue(entity, Math.clamp(newValue, this.minValue(entity), this.maxValue(entity)));
        }

        @Override
        public void reset(LivingEntity entity, AttributeApplicator applicator) {
            // no-op for pseudo vanilla values that are applied immediately
        }

        @Override
        public Number getValue(LivingEntity entity) {
            return this.currentValue(entity);
        }

        protected double minValue(LivingEntity entity) {
            return 0.0D;
        }

        @Override
        public abstract double currentValue(LivingEntity entity);

        protected abstract double referenceValue(LivingEntity entity);

        protected abstract double maxValue(LivingEntity entity);

        protected abstract void setValue(LivingEntity entity, double value);
    }

    public record PseudoAttributeApplyResult(boolean applied, boolean activated) {
        public static final PseudoAttributeApplyResult NOT_APPLIED = new PseudoAttributeApplyResult(false, false);
    }
}

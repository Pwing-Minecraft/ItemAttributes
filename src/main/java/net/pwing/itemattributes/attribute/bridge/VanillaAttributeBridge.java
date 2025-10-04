package net.pwing.itemattributes.attribute.bridge;

import net.pwing.itemattributes.attribute.AttributeApplicator;
import net.pwing.itemattributes.attribute.AttributeOperation;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;

public class VanillaAttributeBridge implements AttributeBridge {
    public static final VanillaAttributeBridge INSTANCE = new VanillaAttributeBridge();

    private VanillaAttributeBridge() {
    }

    @Override
    public void apply(Player player, AttributeApplicator applicator, Number value) {
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
}

package net.pwing.itemattributes.attribute;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.util.Set;

public enum SlotGroup {
    MAIN_HAND(EquipmentSlot.HAND),
    OFF_HAND(EquipmentSlot.OFF_HAND),
    FEET(EquipmentSlot.FEET),
    LEGS(EquipmentSlot.LEGS),
    CHEST(EquipmentSlot.CHEST),
    HEAD(EquipmentSlot.HEAD),
    BODY(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET),
    ALL(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.HAND, EquipmentSlot.OFF_HAND);

    private final Set<EquipmentSlot> slots;

    SlotGroup(EquipmentSlot... slots) {
        this.slots = Set.of(slots);
    }

    public Set<EquipmentSlot> getSlots() {
        return this.slots;
    }

    public EquipmentSlotGroup toSlotGroup() {
        return switch (this) {
            case MAIN_HAND -> EquipmentSlotGroup.HAND;
            case OFF_HAND -> EquipmentSlotGroup.OFFHAND;
            case FEET -> EquipmentSlotGroup.FEET;
            case LEGS -> EquipmentSlotGroup.LEGS;
            case CHEST -> EquipmentSlotGroup.CHEST;
            case HEAD -> EquipmentSlotGroup.HEAD;
            case BODY -> EquipmentSlotGroup.ARMOR;
            case ALL -> EquipmentSlotGroup.ANY;
            default -> throw new IllegalArgumentException("Unknown slot group: " + this);
        };
    }
}

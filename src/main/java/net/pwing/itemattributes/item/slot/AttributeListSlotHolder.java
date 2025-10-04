package net.pwing.itemattributes.item.slot;

import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.attribute.ItemAttribute;
import net.pwing.itemattributes.item.AttributableItem;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AttributeListSlotHolder extends SlotHolder {
    private static final NamespacedKey ATTRIBUTES_LIST_KEY = new NamespacedKey(ItemAttributes.getInstance(), "attributes_list");

    private final List<AttributeSlotHolder> slots = new ArrayList<>();

    public AttributeListSlotHolder(PersistentDataContainer container) {
        super(SlotType.ATTRIBUTE_LIST, container);

        List<PersistentDataContainer> containers = container.get(ATTRIBUTES_LIST_KEY, PersistentDataType.LIST.dataContainers());
        if (containers == null) {
            return;
        }

        for (PersistentDataContainer attributeContainer : containers) {
            this.slots.add(new AttributeSlotHolder(attributeContainer));
        }
    }

    public AttributeListSlotHolder(PersistentDataContainer container, List<AttributeSlotHolder> slots) {
        super(SlotType.ATTRIBUTE_LIST, container);

        this.slots.addAll(slots);
    }

    @Override
    public void save() {
        super.save();

        if (this.slots.isEmpty()) {
            return;
        }

        List<PersistentDataContainer> containers = new ArrayList<>();
        for (AttributeSlotHolder slot : this.slots) {
            slot.save();

            containers.add(slot.getDataContainer());
        }

        this.getDataContainer().set(ATTRIBUTES_LIST_KEY, PersistentDataType.LIST.dataContainers(), containers);
    }

    @Override
    public boolean isEmpty() {
        return this.slots.isEmpty();
    }

    @Override
    protected Component renderSlotInfo(AttributableItem item) {
        if (this.slots.isEmpty()) {
            return Component.empty();
        }

        return Component.empty(); // TODO: Render a slot here?
    }

    @Override
    public List<Component> describeSlotInfo() {
        return this.slots.stream()
                .map(AttributeSlotHolder::describeSlotInfo)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public void applyFromRawArgs(String[] args) throws IllegalArgumentException {
        if (args.length == 0 || args.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid number of arguments. Must be greater than 0 and a multiple of 2");
        }

        for (int i = 0; i < args.length / 2; i++) {
            String attributeKey = args[i * 2];
            String valueString = args[i * 2 + 1];

            ItemAttribute attribute = ItemAttributes.getInstance().getAttributesConfig().getAttributes().get(attributeKey);
            if (attribute == null) {
                throw new IllegalArgumentException("Invalid attribute key: " + attributeKey);
            }

            Number value;
            try {
                value = attribute.getType().convert(valueString);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid value for attribute " + attributeKey + ": " + valueString, e);
            }

            this.slots.add(new AttributeSlotHolder(this.getDataContainer().getAdapterContext().newPersistentDataContainer(), attribute, value));
        }
    }

    @Override
    public void applyFromHolder(SlotHolder holder) {
        if (holder instanceof AttributeSlotHolder toApply) {
            super.applyFromHolder(holder);

            if (toApply.getAttribute() == null || toApply.getValue() == null) {
                return;
            }

            this.slots.clear();
            this.slots.add(toApply);
            return;
        } else if (holder instanceof AttributeListSlotHolder toApply) {
            super.applyFromHolder(holder);

            this.slots.clear();

            for (AttributeSlotHolder slot : toApply.getSlots()) {
                AttributeSlotHolder newSlot = new AttributeSlotHolder(holder.getDataContainer().getAdapterContext().newPersistentDataContainer(), slot.getAttribute(), slot.getValue());
                this.slots.add(newSlot);
            }

            return;
        }

        throw new IllegalArgumentException("Cannot apply from a non-attribute or attribute list slot holder!");
    }

    public List<AttributeSlotHolder> getSlots() {
        return this.slots;
    }
}

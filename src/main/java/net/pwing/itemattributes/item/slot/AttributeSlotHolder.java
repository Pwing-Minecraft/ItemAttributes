package net.pwing.itemattributes.item.slot;

import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.attribute.AttributeCalculator;
import net.pwing.itemattributes.attribute.ItemAttribute;
import net.pwing.itemattributes.item.AttributableItem;
import net.pwing.itemattributes.message.Messages;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class AttributeSlotHolder extends SlotHolder {
    private static final NamespacedKey ATTRIBUTE_TYPE_KEY = new NamespacedKey(ItemAttributes.getInstance(), "attribute_type");
    private static final NamespacedKey ATTRIBUTE_VALUE_KEY = new NamespacedKey(ItemAttributes.getInstance(), "attribute_value");

    private ItemAttribute attribute;
    private Number value;

    public AttributeSlotHolder(PersistentDataContainer container) {
        super(SlotType.ATTRIBUTE, container);

        String attributeKey = container.get(ATTRIBUTE_TYPE_KEY, PersistentDataType.STRING);
        if (attributeKey == null) {
            return;
        }

        this.attribute = ItemAttributes.getInstance().getAttributesConfig().getAttributes().get(attributeKey);
        if (this.attribute == null) {
            return;
        }

        this.value = (Number) container.get(ATTRIBUTE_VALUE_KEY, this.attribute.getType().getStorageType());
    }

    public AttributeSlotHolder(PersistentDataContainer container, ItemAttribute attribute, Number value) {
        super(SlotType.ATTRIBUTE, container);

        this.attribute = attribute;
        this.value = value;
    }

    @Override
    public void save() {
        super.save();

        if (this.attribute == null) {
            return;
        }

        this.getDataContainer().set(ATTRIBUTE_TYPE_KEY, PersistentDataType.STRING, this.attribute.getId());
        this.getDataContainer().set(ATTRIBUTE_VALUE_KEY, this.attribute.getType().getStorageType(), this.attribute.getType().convert(this.value));
    }

    @Override
    public boolean isEmpty() {
        return this.attribute == null || this.value == null;
    }

    @Override
    protected Component renderSlotInfo(AttributableItem item) {
        if (this.value == null) {
            return Component.empty();
        }

        return AttributeCalculator.computeOperators(item.render(this.attribute.getDisplay()), this.value);
    }

    @Override
    public List<Component> describeSlotInfo() {
        return List.of(Component.text("Attribute: ", Messages.PRIMARY_COLOR).append(AttributeCalculator.computeOperators(this.attribute.getDisplay(), this.value)));
    }

    @Override
    public void applyFromRawArgs(String[] args) throws IllegalArgumentException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Invalid number of arguments! Expected 2, got " + args.length);
        }

        String attributeKey = args[0];
        String valueString = args[1];

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

        this.attribute = attribute;
        this.value = value;
    }

    @Override
    public void applyFromHolder(SlotHolder holder) {
        if (!(holder instanceof AttributeSlotHolder toApply)) {
            throw new IllegalArgumentException("Cannot apply from a non-attribute slot holder!");
        }

        super.applyFromHolder(holder);

        if (toApply.attribute == null || toApply.value == null) {
            return;
        }

        this.attribute = toApply.attribute;
        this.value = toApply.value;
    }

    public ItemAttribute getAttribute() {
        return this.attribute;
    }

    public Number getValue() {
        return this.value;
    }
}

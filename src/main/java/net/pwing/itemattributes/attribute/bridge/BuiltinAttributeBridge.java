package net.pwing.itemattributes.attribute.bridge;

import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.attribute.AttributeApplicator;
import net.pwing.itemattributes.attribute.AttributeCalculator;
import net.pwing.itemattributes.attribute.ItemAttribute;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class BuiltinAttributeBridge implements AttributeBridge {
    public static final BuiltinAttributeBridge INSTANCE = new BuiltinAttributeBridge();

    private BuiltinAttributeBridge() {
    }

    @Override
    public void apply(Player player, AttributeApplicator applicator, Number value) {
        // no-op
    }

    @Override
    public void reset(Player player, AttributeApplicator applicator) {
        // no-op
    }

    @Override
    public Number getValue(Player player, NamespacedKey key) {
        String attributeKey = key.getKey();
        ItemAttribute attribute = ItemAttributes.getInstance().getAttributesConfig().getAttributes().get(attributeKey);
        if (attribute == null) {
            return 0;
        }

        return AttributeCalculator.calculateFullAttributeValue(player, ItemAttributes.getInstance().getAttributeManager(), attribute);
    }
}

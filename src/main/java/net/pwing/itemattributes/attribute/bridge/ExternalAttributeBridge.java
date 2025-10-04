package net.pwing.itemattributes.attribute.bridge;

import net.pwing.itemattributes.attribute.AttributeApplicator;
import net.pwing.itemattributes.feature.attribute.CustomAttributes;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class ExternalAttributeBridge implements AttributeBridge {
    public static final ExternalAttributeBridge INSTANCE = new ExternalAttributeBridge();

    private ExternalAttributeBridge() {
    }

    @Override
    public void apply(Player player, AttributeApplicator applicator, Number value) {
        CustomAttributes.apply(player, applicator, value);
    }

    @Override
    public void reset(Player player, AttributeApplicator applicator) {
        CustomAttributes.reset(player, applicator.getKey());
    }

    @Override
    public Number getValue(Player player, NamespacedKey key) {
        return CustomAttributes.getValue(player, key);
    }
}

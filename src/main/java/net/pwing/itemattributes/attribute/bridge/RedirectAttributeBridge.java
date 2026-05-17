package net.pwing.itemattributes.attribute.bridge;

import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.attribute.AttributeApplicator;
import net.pwing.itemattributes.attribute.ItemAttribute;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class RedirectAttributeBridge implements AttributeBridge {
    public static final RedirectAttributeBridge INSTANCE = new RedirectAttributeBridge();

    private RedirectAttributeBridge() {
    }

    @Override
    public void apply(Player player, AttributeApplicator applicator, Number value) {
        AttributeApplicator destination = applicator.getResolvedDestination();
        if (destination == null) {
            return;
        }

        destination.getType().apply(player, destination, value);
    }

    @Override
    public void reset(Player player, AttributeApplicator applicator) {
        AttributeApplicator destination = applicator.getResolvedDestination();
        if (destination == null) {
            return;
        }

        destination.getType().reset(player, destination);
    }

    @Override
    public Number getValue(Player player, NamespacedKey key) {
        ItemAttribute attribute = ItemAttributes.getInstance().getAttributesConfig().getAttributes().get(key.getKey());
        if (attribute == null) {
            return 0;
        }

        AttributeApplicator sourceApplicator = attribute.getAttribute().getSourceOrSelf();
        return sourceApplicator.getType().getValue(player, sourceApplicator.getKey());
    }
}



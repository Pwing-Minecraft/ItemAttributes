package net.pwing.itemattributes.attribute.bridge;

import net.pwing.itemattributes.attribute.AttributeApplicator;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public interface AttributeBridge {

    void apply(Player player, AttributeApplicator applicator, Number value);

    void reset(Player player, AttributeApplicator applicator);

    Number getValue(Player player, NamespacedKey key);
}

package net.pwing.itemattributes.feature.attribute;

import net.pwing.itemattributes.attribute.AttributeApplicator;
import net.pwing.itemattributes.feature.FeatureInstance;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public interface AttributesFeature extends FeatureInstance {

    Number getValue(Player player, NamespacedKey attributeKey);

    void apply(Player player, AttributeApplicator applicator, Number value);

    void reset(Player player, NamespacedKey attributeKey);
}

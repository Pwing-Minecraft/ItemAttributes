package net.pwing.itemattributes.feature.placeholder;

import net.pwing.itemattributes.feature.FeatureInstance;
import org.bukkit.entity.Player;

public interface PlaceholdersFeature extends FeatureInstance {

    String replacePlaceholders(Player player, String message);
}

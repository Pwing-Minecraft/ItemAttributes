package net.pwing.itemattributes.feature.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.feature.PluginFeature;
import net.pwing.itemattributes.feature.placeholder.papi.ItemAttributesExpansion;
import org.bukkit.entity.Player;

public class PapiPlaceholderFeature extends PluginFeature<PlaceholdersFeature> implements PlaceholdersFeature {

    public PapiPlaceholderFeature() {
        super("PlaceholderAPI");

        new ItemAttributesExpansion(ItemAttributes.getInstance()).register();
    }

    @Override
    public String replacePlaceholders(Player player, String message) {
        return PlaceholderAPI.setPlaceholders(player, message);
    }
}

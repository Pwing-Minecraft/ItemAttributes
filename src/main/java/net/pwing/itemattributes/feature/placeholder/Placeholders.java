package net.pwing.itemattributes.feature.placeholder;

import net.pwing.itemattributes.feature.FeatureController;
import net.pwing.itemattributes.feature.PluginFeature;
import org.bukkit.Bukkit;

public final class Placeholders extends FeatureController<PluginFeature<PlaceholdersFeature>> {

    public static void init() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            register(new PapiPlaceholderFeature());
        }
    }

    public static <T extends PluginFeature<PlaceholdersFeature> & PlaceholdersFeature> void register(T feature) {
        registerFeature(PlaceholdersFeature.class, feature);
    }
}

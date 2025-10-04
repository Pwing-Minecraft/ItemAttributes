package net.pwing.itemattributes.feature.entities;

import net.pwing.itemattributes.feature.FeatureController;
import net.pwing.itemattributes.feature.PluginFeature;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.Locale;

public class Entities extends FeatureController<PluginFeature<EntitiesFeature>> {

    public static void init() {
        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            register(new MythicMobsFeature());
        }
    }

    public static boolean isEntityType(NamespacedKey key, Entity entity) {
        return getFeature(key).isEntityType(key, entity);
    }

    public static <T extends PluginFeature<EntitiesFeature> & EntitiesFeature> void register(T feature) {
        registerFeature(EntitiesFeature.class, feature);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static <T extends PluginFeature<EntitiesFeature> & EntitiesFeature> EntitiesFeature getFeature(NamespacedKey key) {
        // Fast-track vanilla
        if (key.getNamespace().equals(NamespacedKey.MINECRAFT)) {
            return VanillaEntitiesFeature.INSTANCE;
        }

        List<T> features = (List) getFeatures(EntitiesFeature.class);
        for (T feature : features) {
            if (!feature.isEnabled()) {
                continue;
            }

            String pluginNamespace = feature.getPlugin().getName().toLowerCase(Locale.ROOT);
            if (key.getNamespace().equals(pluginNamespace)) {
                return feature;
            }
        }

        return VanillaEntitiesFeature.INSTANCE;
    }
}

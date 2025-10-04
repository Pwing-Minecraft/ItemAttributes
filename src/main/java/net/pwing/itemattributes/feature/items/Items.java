package net.pwing.itemattributes.feature.items;

import net.pwing.itemattributes.feature.FeatureController;
import net.pwing.itemattributes.feature.PluginFeature;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;

public final class Items extends FeatureController<PluginFeature<ItemsFeature>> {

    public static void init() {
        if (Bukkit.getPluginManager().isPluginEnabled("ItemsAdder")) {
            register(new ItemsAdderItemsFeature());
        }

        if (Bukkit.getPluginManager().isPluginEnabled("MythicCrucible")) {
            register(new MythicCrucibleItemsFeature());
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Oraxen")) {
            register(new OraxenItemsFeature());
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Nexo")) {
            register(new NexoItemsFeature());
        }
    }

    public static ItemStack createItem(NamespacedKey key) {
        return getFeature(key).createItem(key);
    }

    public static boolean isSameItemType(ItemStack item1, ItemStack item2) {
        List<ItemsFeature> features = getFeatures(ItemsFeature.class);
        for (ItemsFeature feature : features) {
            if (!feature.isEnabled()) {
                continue;
            }

            if (feature.isFeatureItem(item1) && feature.isFeatureItem(item2)) {
                return feature.isSameItemType(item1, item2);
            }
        }

        return VanillaItemsFeature.INSTANCE.isSameItemType(item1, item2);
    }

    public static <T extends PluginFeature<ItemsFeature> & ItemsFeature> void register(T feature) {
        registerFeature(ItemsFeature.class, feature);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static <T extends PluginFeature<ItemsFeature> & ItemsFeature> ItemsFeature getFeature(NamespacedKey key) {
        // Fast-track vanilla
        if (key.getNamespace().equals(NamespacedKey.MINECRAFT)) {
            return VanillaItemsFeature.INSTANCE;
        }

        List<T> features = (List) getFeatures(ItemsFeature.class);
        for (T feature : features) {
            if (!feature.isEnabled()) {
                continue;
            }

            String pluginNamespace = feature.getPlugin().getName().toLowerCase(Locale.ROOT);
            if (key.getNamespace().equals(pluginNamespace)) {
                return feature;
            }
        }

        return VanillaItemsFeature.INSTANCE;
    }
}

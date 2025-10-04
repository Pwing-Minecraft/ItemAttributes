package net.pwing.itemattributes.feature.items;

import net.pwing.itemattributes.feature.FeatureInstance;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public interface ItemsFeature extends FeatureInstance {

    ItemStack createItem(NamespacedKey key);

    boolean isFeatureItem(ItemStack item);

    boolean isSameItemType(ItemStack item, ItemStack other);
}

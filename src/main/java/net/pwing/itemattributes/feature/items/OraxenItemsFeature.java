package net.pwing.itemattributes.feature.items;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import net.pwing.itemattributes.feature.PluginFeature;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class OraxenItemsFeature extends PluginFeature<ItemsFeature> implements ItemsFeature {

    public OraxenItemsFeature() {
        super("Oraxen");
    }

    @Override
    public ItemStack createItem(NamespacedKey key) {
        return OraxenItems.getOptionalItemById(key.getKey())
                .map(ItemBuilder::build)
                .orElse(null);
    }

    @Override
    public boolean isFeatureItem(ItemStack item) {
        String id = OraxenItems.getIdByItem(item);
        if (id == null) {
            return false;
        }

        return OraxenItems.exists(item);
    }

    @Override
    public boolean isSameItemType(ItemStack item, ItemStack other) {
        String itemId = OraxenItems.getIdByItem(item);
        String otherId = OraxenItems.getIdByItem(other);
        if (itemId == null || otherId == null) {
            return false;
        }

        return itemId.equals(otherId);
    }
}

package net.pwing.itemattributes.feature.items;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import net.pwing.itemattributes.feature.PluginFeature;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class NexoItemsFeature extends PluginFeature<ItemsFeature> implements ItemsFeature {

    public NexoItemsFeature() {
        super("Nexo");
    }

    @Override
    public ItemStack createItem(NamespacedKey key) {
        ItemBuilder builder = NexoItems.itemFromId(key.getKey());
        if (builder == null) {
            return null;
        }

        return builder.build();
    }

    @Override
    public boolean isFeatureItem(ItemStack item) {
        String id = NexoItems.idFromItem(item);
        if (id == null) {
            return false;
        }

        return NexoItems.exists(id);
    }

    @Override
    public boolean isSameItemType(ItemStack item, ItemStack other) {
        String itemId = NexoItems.idFromItem(item);
        String otherId = NexoItems.idFromItem(other);
        if (itemId == null || otherId == null) {
            return false;
        }

        return itemId.equals(otherId);
    }
}

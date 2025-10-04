package net.pwing.itemattributes.feature.items;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import net.pwing.itemattributes.feature.PluginFeature;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class MythicCrucibleItemsFeature extends PluginFeature<ItemsFeature> implements ItemsFeature {

    public MythicCrucibleItemsFeature() {
        super("MythicCrucible");
    }

    @Override
    public ItemStack createItem(NamespacedKey key) {
        return MythicBukkit.inst().getItemManager().getItem(key.getKey())
                .map(item -> BukkitAdapter.adapt(item.generateItemStack(1)))
                .orElse(null);
    }

    @Override
    public boolean isFeatureItem(ItemStack item) {
        return MythicBukkit.inst().getItemManager().isMythicItem(item);
    }

    @Override
    public boolean isSameItemType(ItemStack item, ItemStack other) {
        String itemType = MythicBukkit.inst().getItemManager().getMythicTypeFromItem(item);
        String otherType = MythicBukkit.inst().getItemManager().getMythicTypeFromItem(other);
        if (itemType == null || otherType == null) {
            return false;
        }

        return itemType.equals(otherType);
    }
}

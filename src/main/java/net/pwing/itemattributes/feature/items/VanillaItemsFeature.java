package net.pwing.itemattributes.feature.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

class VanillaItemsFeature implements ItemsFeature {
    static final VanillaItemsFeature INSTANCE = new VanillaItemsFeature();

    private VanillaItemsFeature() {
    }

    @Override
    public ItemStack createItem(NamespacedKey key) {
        ItemType itemType = Registry.ITEM.get(key);
        if (itemType == null) {
            return new ItemStack(Material.AIR);
        }

        return itemType.createItemStack();
    }

    @Override
    public boolean isFeatureItem(ItemStack item) {
        return true;
    }

    @Override
    public boolean isSameItemType(ItemStack item, ItemStack other) {
        return item.getType() == other.getType();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

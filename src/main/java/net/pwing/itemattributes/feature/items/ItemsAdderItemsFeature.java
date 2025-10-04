package net.pwing.itemattributes.feature.items;

import dev.lone.itemsadder.api.CustomStack;
import net.pwing.itemattributes.feature.PluginFeature;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderItemsFeature extends PluginFeature<ItemsFeature> implements ItemsFeature {

    public ItemsAdderItemsFeature() {
        super("ItemsAdder");
    }

    @Override
    public ItemStack createItem(NamespacedKey key) {
        CustomStack customStack = CustomStack.getInstance(key.getKey());
        return customStack == null ? null : customStack.getItemStack();
    }

    @Override
    public boolean isFeatureItem(ItemStack item) {
        return CustomStack.byItemStack(item) != null;
    }

    @Override
    public boolean isSameItemType(ItemStack item, ItemStack other) {
        CustomStack customStack1 = CustomStack.byItemStack(item);
        CustomStack customStack2 = CustomStack.byItemStack(other);
        if (customStack1 == null || customStack2 == null) {
            return false;
        }

        return customStack1.getNamespacedID().equals(customStack2.getNamespacedID());
    }
}

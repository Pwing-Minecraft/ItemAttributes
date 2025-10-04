package net.pwing.itemattributes.item;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemUtils {

    public static void hideVisualAttributes(Material type, ItemMeta meta) {
        // And now for a slightly hacky solution to hide attributes (since we only modify attributes on the player,
        // and not the item itself, we need to set the attributes to default)
        meta.setAttributeModifiers(type.getDefaultAttributeModifiers(EquipmentSlot.HAND));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        // NOTE: This will be fixed in later Minecraft versions, but this solves the issue for now
    }
}

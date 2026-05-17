package net.pwing.itemattributes.attribute;

import net.pwing.itemattributes.armor.PlayerArmorChangeEvent;
import net.pwing.itemattributes.util.DelayedProcessor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AttributeListener implements Listener, DelayedProcessor {
    private final AttributeManager manager;

    public AttributeListener(AttributeManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArmorChange(PlayerArmorChangeEvent event) {
        this.postProcess(() -> {
            this.manager.refreshAttributes(event.getPlayer(), event.getOldItem());
            this.manager.refreshAttributes(event.getPlayer(), event.getNewItem());
        });
    }

    @EventHandler
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        this.postProcess(() -> {
            ItemStack oldItem = event.getOffHandItem();
            if (oldItem == null) {
                oldItem = new ItemStack(Material.AIR);
            }

            ItemStack newItem = event.getMainHandItem();
            if (newItem == null) {
                newItem = new ItemStack(Material.AIR);
            }

            this.manager.refreshAttributes(event.getPlayer(), oldItem);
            this.manager.refreshAttributes(event.getPlayer(), newItem);
        });
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        this.postProcess(() -> {
            ItemStack oldItem = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
            if (oldItem == null) {
                oldItem = new ItemStack(Material.AIR);
            }

            ItemStack newItem = event.getPlayer().getInventory().getItem(event.getNewSlot());
            if (newItem == null) {
                newItem = new ItemStack(Material.AIR);
            }

            this.manager.refreshAttributes(event.getPlayer(), oldItem);
            this.manager.refreshAttributes(event.getPlayer(), newItem);
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        boolean numberKeySwap = event.getClick() == ClickType.NUMBER_KEY && event.getHotbarButton() >= 0;
        boolean quickbarClick = event.getSlotType() == InventoryType.SlotType.QUICKBAR;
        if (!numberKeySwap && !quickbarClick) {
            return;
        }

        ItemStack oldClickedItem = normalize(event.getCurrentItem());
        ItemStack oldCursorItem = normalize(event.getCursor());

        int hotbarSlot = event.getHotbarButton();
        ItemStack oldHotbarItem = hotbarSlot >= 0 ? normalize(player.getInventory().getItem(hotbarSlot)) : new ItemStack(Material.AIR);

        this.postProcess(() -> {
            List<ItemStack> refreshed = new ArrayList<>(4);
            ItemStack newClickedItem = normalize(event.getClickedInventory() == null ? null : event.getClickedInventory().getItem(event.getSlot()));
            this.refresh(player, oldClickedItem, newClickedItem, refreshed);

            if (numberKeySwap) {
                ItemStack newHotbarItem = normalize(player.getInventory().getItem(hotbarSlot));
                this.refresh(player, oldHotbarItem, newHotbarItem, refreshed);
            } else {
                ItemStack newCursorItem = normalize(event.getCursor());
                this.refresh(player, oldCursorItem, newCursorItem, refreshed);
            }
        });
    }

    private void refresh(Player player, ItemStack oldItem, ItemStack newItem, List<ItemStack> refreshed) {
        if (isSameItem(oldItem, newItem)) {
            return;
        }

        this.refresh(player, oldItem, refreshed);
        this.refresh(player, newItem, refreshed);
    }

    private void refresh(Player player, ItemStack item, List<ItemStack> refreshed) {
        if (item.getType() == Material.AIR || item.getItemMeta() == null) {
            return;
        }

        for (ItemStack existing : refreshed) {
            if (existing.isSimilar(item)) {
                return;
            }
        }

        refreshed.add(item);
        this.manager.refreshAttributes(player, item);
    }

    private static ItemStack normalize(ItemStack item) {
        return item == null ? new ItemStack(Material.AIR) : item.clone();
    }

    private static boolean isSameItem(ItemStack first, ItemStack second) {
        return (first.getType() == Material.AIR && second.getType() == Material.AIR) || first.isSimilar(second);
    }
}

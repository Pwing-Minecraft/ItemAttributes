package net.pwing.itemattributes.attribute;

import net.pwing.itemattributes.armor.PlayerArmorChangeEvent;
import net.pwing.itemattributes.util.DelayedProcessor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.QUICKBAR) {
            this.postProcess(() -> {
                ItemStack oldItem = event.getCurrentItem();
                if (oldItem == null) {
                    oldItem = new ItemStack(Material.AIR);
                }

                ItemStack newItem = event.getCursor();
                if (newItem == null) {
                    newItem = new ItemStack(Material.AIR);
                }

                this.manager.refreshAttributes((Player) event.getWhoClicked(), oldItem);
                this.manager.refreshAttributes((Player) event.getWhoClicked(), newItem);
            });
        }
    }
}

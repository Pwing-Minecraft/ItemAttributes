package net.pwing.itemattributes.util.menu;

import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.message.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class InventoryBuilder {
    private int size;
    private Component title;

    private final Map<Integer, ItemStack> items = new HashMap<>();
    private final Map<Integer, ClickHandler> clickHandlers = new HashMap<>();

    private InventoryBuilder() {
    }

    public InventoryBuilder size(int size) {
        this.size = size;
        return this;
    }

    public InventoryBuilder title(Component title) {
        this.title = title;
        return this;
    }

    public InventoryBuilder item(int slot, ItemStack item, ClickHandler clickHandler) {
        this.items.put(slot, item);
        this.clickHandlers.put(slot, clickHandler);
        return this;
    }

    public InventoryBuilder item(int slot, ItemStack item) {
        this.items.put(slot, item);
        return this;
    }

    public Inventory build() {
        if (this.size <= 0) {
            throw new IllegalArgumentException("Inventory size must be greater than 0");
        }

        if (this.title == null) {
            throw new IllegalArgumentException("Inventory title cannot be null");
        }

        Inventory inventory = Bukkit.createInventory(null, this.size, TextUtils.toLegacy(this.title));
        for (Map.Entry<Integer, ItemStack> entry : this.items.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue());
        }

        if (!this.clickHandlers.isEmpty()) {
            this.registerListeners(inventory);
        }

        return inventory;
    }

    public void open(Player player) {
        Inventory inventory = this.build();
        player.openInventory(inventory);
    }

    public static InventoryBuilder builder() {
        return new InventoryBuilder();
    }

    private void registerListeners(Inventory inventory) {
        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onClick(InventoryClickEvent event) {
                if (!event.getInventory().equals(inventory)) {
                    return;
                }

                event.setCancelled(true);

                ClickHandler handler = InventoryBuilder.this.clickHandlers.get(event.getSlot());
                if (handler != null) {
                    handler.onClick((Player) event.getWhoClicked(), event.getClick(), event.getCurrentItem());
                }
            }

            @EventHandler
            public void onClose(InventoryCloseEvent event) {
                if (!event.getInventory().equals(inventory)) {
                    return;
                }

                int viewers = (event.getInventory().getViewers().size() - 1); // -1 for the player who closed the inventory
                if (viewers > 0) {
                    return;
                }

                HandlerList.unregisterAll(this);
            }
        }, ItemAttributes.getInstance());
    }
}

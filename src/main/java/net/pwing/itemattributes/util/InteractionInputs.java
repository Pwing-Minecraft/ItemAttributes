package net.pwing.itemattributes.util;

import net.pwing.itemattributes.ItemAttributes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

public class InteractionInputs {

    public static abstract class ChatInput {

        public ChatInput(Player player) {
            this(player, "Invalid input, please try again.");
        }

        public ChatInput(Player player, String invalidInputMessage) {
            Listener listener = new Listener() {

                @EventHandler
                public void onChat(AsyncPlayerChatEvent event) {
                    if (!player.equals(event.getPlayer())) {
                        return;
                    }

                    if ("cancel".equalsIgnoreCase(event.getMessage())) {
                        event.setCancelled(true);

                        player.sendMessage(ChatColor.GREEN + "Cancelled!");
                        HandlerList.unregisterAll(this);

                        Bukkit.getScheduler().runTaskLater(ItemAttributes.getInstance(), () -> onCancel(), 1);
                        return;
                    }

                    event.setCancelled(true);
                    if (!isValidChatInput(event.getMessage())) {
                        player.sendMessage(ChatColor.RED + invalidInputMessage);
                        return;
                    }

                    String message = ChatColor.stripColor(event.getMessage());

                    // Run task synchronously since chat is async
                    Bukkit.getScheduler().runTask(ItemAttributes.getInstance(), () -> {
                        onChatInput(message);
                    });

                    HandlerList.unregisterAll(this);
                }
            };

            Bukkit.getPluginManager().registerEvents(listener, ItemAttributes.getInstance());
        }

        public abstract void onChatInput(String input);

        public void onCancel() {
        }

        public boolean isValidChatInput(String input) {
            return true;
        }
    }

    public static abstract class InventoryInput {

        public InventoryInput(Player player) {
            Listener listener = new Listener() {

                @EventHandler
                public void onChat(AsyncPlayerChatEvent event) {
                    if (!player.equals(event.getPlayer())) {
                        return;
                    }

                    if ("cancel".equalsIgnoreCase(event.getMessage())) {
                        event.setCancelled(true);

                        player.sendMessage(ChatColor.GREEN + "Cancelled!");
                        HandlerList.unregisterAll(this);

                        Bukkit.getScheduler().runTaskLater(ItemAttributes.getInstance(), () -> onCancel(), 1);
                    }
                }

                @EventHandler
                public void onInteract(InventoryClickEvent event) {
                    if (!player.equals(event.getWhoClicked())) {
                        return;
                    }

                    if (!player.getInventory().equals(event.getClickedInventory())) {
                        player.sendMessage(ChatColor.RED + "Interacted with inventory that was not own.. cancelling item selection.");
                        HandlerList.unregisterAll(this);
                        return;
                    }

                    if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                        return;
                    }

                    ItemStack currentItem = event.getCurrentItem().clone();

                    // Need to run a tick later so Bukkit can handle the event cancellation
                    Bukkit.getScheduler().runTaskLater(ItemAttributes.getInstance(), () -> {
                        onInventoryInteract(currentItem);
                    }, 1);

                    // This is needed to prevent the item from being picked up in
                    // creative. Seems overkill but Bukkit(TM)
                    event.setCancelled(true);
                    event.getView().setCursor(null);
                    player.updateInventory();

                    HandlerList.unregisterAll(this);
                }
            };

            Bukkit.getPluginManager().registerEvents(listener, ItemAttributes.getInstance());
        }

        public abstract void onInventoryInteract(ItemStack item);

        public void onCancel() {
        }
    }
}

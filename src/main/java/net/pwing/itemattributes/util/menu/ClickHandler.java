package net.pwing.itemattributes.util.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface ClickHandler {
        
    void onClick(Player player, ClickType clickType, ItemStack item);

    static ClickHandler onClick(Runnable runnable) {
        return (player, clickType, stack) -> runnable.run();
    }

    static ClickHandler onClick(Consumer<Player> consumer) {
        return (player, clickType, stack) -> consumer.accept(player);
    }

    static ClickHandler onClick(BiConsumer<Player, ItemStack> consumer) {
        return (player, clickType, stack) -> consumer.accept(player, stack);
    }
}

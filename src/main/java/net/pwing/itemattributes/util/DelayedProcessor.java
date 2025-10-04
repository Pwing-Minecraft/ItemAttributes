package net.pwing.itemattributes.util;

import net.pwing.itemattributes.ItemAttributes;
import org.bukkit.Bukkit;

public interface DelayedProcessor {

    default void postProcess(Runnable runnable) {
        Bukkit.getScheduler().runTaskLater(ItemAttributes.getInstance(), runnable, 1);
    }
}

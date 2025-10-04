package net.pwing.itemattributes.armor;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PlayerArmorChangeEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final EquipmentSlot slot;
    private final ItemStack oldItem;
    private final ItemStack newItem;

    private final ItemStack oldItemSnapshot;
    private final ItemStack newItemSnapshot;

    private boolean cancelled;

    public PlayerArmorChangeEvent(Player player, EquipmentSlot slot, ItemStack oldItem, ItemStack newItem) {
        super(player);
        this.slot = slot;
        this.oldItem = oldItem;
        this.newItem = newItem;

        this.oldItemSnapshot = oldItem == null ? null : oldItem.clone();
        this.newItemSnapshot = newItem == null ? null : newItem.clone();
    }

    public EquipmentSlot getSlot() {
        return this.slot;
    }

    @Nullable
    public ItemStack getOldItem() {
        return this.oldItemSnapshot;
    }

    @Nullable
    public ItemStack mutateOldItem() {
        return this.oldItem;
    }

    @Nullable
    public ItemStack getNewItem() {
        return this.newItemSnapshot;
    }

    @Nullable
    public ItemStack mutateNewItem() {
        return this.newItem;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

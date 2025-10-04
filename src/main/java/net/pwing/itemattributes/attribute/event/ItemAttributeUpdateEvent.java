package net.pwing.itemattributes.attribute.event;

import net.pwing.itemattributes.attribute.ItemAttribute;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemAttributeUpdateEvent extends AttributeUpdateEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final ItemStack itemStack;

    public ItemAttributeUpdateEvent(ItemAttribute attribute, ItemStack itemStack, Number value) {
        super(attribute, itemStack.getItemMeta(), value);

        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

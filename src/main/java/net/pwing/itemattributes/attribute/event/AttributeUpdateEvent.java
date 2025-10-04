package net.pwing.itemattributes.attribute.event;

import net.pwing.itemattributes.attribute.ItemAttribute;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.persistence.PersistentDataHolder;
import org.jetbrains.annotations.NotNull;

public class AttributeUpdateEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final ItemAttribute attribute;
    private final PersistentDataHolder holder;
    private final Number value;

    public AttributeUpdateEvent(ItemAttribute attribute, PersistentDataHolder holder, Number value) {
        this.attribute = attribute;
        this.holder = holder;
        this.value = value;
    }

    public ItemAttribute getAttribute() {
        return this.attribute;
    }

    public PersistentDataHolder getHolder() {
        return this.holder;
    }

    public Number getValue() {
        return this.value;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

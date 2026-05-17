package net.pwing.itemattributes.attribute.event;

import net.pwing.itemattributes.attribute.ItemAttribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerAttributeApplyEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final ItemAttribute attribute;
    private Number value;
    private final boolean supportsCancellation;

    private boolean cancelled;

    public PlayerAttributeApplyEvent(@NotNull Player who, ItemAttribute attribute, Number value) {
        this(who, attribute, value, false);
    }

    public PlayerAttributeApplyEvent(@NotNull Player who, ItemAttribute attribute, Number value, boolean supportsCancellation) {
        super(who);

        this.attribute = attribute;
        this.value = value;
        this.supportsCancellation = supportsCancellation;
    }

    public ItemAttribute getAttribute() {
        return this.attribute;
    }

    public Number getValue() {
        return this.value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public boolean supportsCancellation() {
        return this.supportsCancellation;
    }
}

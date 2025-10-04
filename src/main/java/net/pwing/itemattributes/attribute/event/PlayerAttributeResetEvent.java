package net.pwing.itemattributes.attribute.event;

import net.pwing.itemattributes.attribute.ItemAttribute;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerAttributeResetEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final ItemAttribute attribute;
    private final Number value;

    public PlayerAttributeResetEvent(@NotNull Player who, ItemAttribute attribute, Number value) {
        super(who);

        this.attribute = attribute;
        this.value = value;
    }

    public ItemAttribute getAttribute() {
        return this.attribute;
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

package net.pwing.itemattributes.attribute.event;

import net.pwing.itemattributes.attribute.ItemAttribute;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerAttributeApplyEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final ItemAttribute attribute;
    private Number value;

    public PlayerAttributeApplyEvent(@NotNull Player who, ItemAttribute attribute, Number value) {
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
}

package net.pwing.itemattributes.item.event;

import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.item.AttributableItem;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemRenderEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final AttributableItem item;
    private List<Component> renderedLines;

    public ItemRenderEvent(AttributableItem item, List<Component> renderedLines) {
        this.item = item;
        this.renderedLines = renderedLines;
    }

    public AttributableItem getItem() {
        return this.item;
    }

    public List<Component> getRenderedLines() {
        return List.copyOf(this.renderedLines);
    }

    public void setRenderedLines(List<Component> renderedLines) {
        this.renderedLines = renderedLines;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

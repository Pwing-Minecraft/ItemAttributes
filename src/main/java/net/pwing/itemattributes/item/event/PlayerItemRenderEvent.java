package net.pwing.itemattributes.item.event;

import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.item.AttributableItem;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerItemRenderEvent extends ItemRenderEvent {

    private final Player player;

    public PlayerItemRenderEvent(Player player, AttributableItem item, List<Component> renderedLines) {
        super(item, renderedLines);

        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }
}

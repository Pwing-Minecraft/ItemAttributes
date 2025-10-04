package net.pwing.itemattributes.item.lore;

import me.redned.config.ConfigOption;
import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.item.AttributableItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpacerLoreCreator extends LoreCreator {

    @ConfigOption(name = "lines", description = "The amount of lines to display in the lore.")
    private int lines = 1;

    @Override
    public List<Component> createLines(AttributableItem item, @Nullable Player viewer) {
        List<Component> lines = new ArrayList<>(this.lines);
        for (int i = 0; i < this.lines; i++) {
            lines.add(Component.space());
        }

        return lines;
    }
}

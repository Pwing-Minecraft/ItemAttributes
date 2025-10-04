package net.pwing.itemattributes.item.lore;

import me.redned.config.ConfigOption;
import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.item.AttributableItem;
import net.pwing.itemattributes.message.TextUtils;
import net.pwing.itemattributes.util.IntRange;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SimpleLoreCreator extends LoreCreator {

    @ConfigOption(name = "header", description = "The header to display before the lines.")
    private String header;

    @ConfigOption(name = "lines", description = "The lines to display in the lore.", required = true)
    private List<String> lines;

    @ConfigOption(name = "footer", description = "The header to display after the lines.")
    private String footer;

    @ConfigOption(name = "max-characters", description = "The maximum amount of characters to display in the lore.")
    private int maxCharacters = -1;

    @ConfigOption(name = "range", description = "The range of lines to render in the lore.")
    private IntRange range;


    @Override
    public List<Component> createLines(AttributableItem item, @Nullable Player viewer) {
        List<Component> list = this.lines.stream()
                .map(item::renderFromTemplate)
                .flatMap(line -> TextUtils.splitComponentAtIntervals(line, this.maxCharacters == -1 ? Integer.MAX_VALUE : this.maxCharacters, null).stream())
                .toList();

        // Remove lines outside the range
        if (this.range != null) {
            int min = this.range.getMin();
            int max = this.range.getMax();
            int size = list.size();

            if (min < 0) {
                min = 0;
            }
            if (max > size) {
                max = size;
            }

            List<Component> subList = new ArrayList<>(list.subList(min, max));

            // If the displayed sublist includes the first item in the list
            if (this.header != null && min == 0 && size > 0) {
                subList.addFirst(item.renderFromTemplate(this.header));
            }

            // If the displayed sublist includes the last item in the list
            if (this.footer != null && max == size && max > min) {
                subList.addLast(item.renderFromTemplate(this.footer));
            }

            return subList;
        }

        return list;
    }
}

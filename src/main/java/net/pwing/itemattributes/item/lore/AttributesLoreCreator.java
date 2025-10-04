package net.pwing.itemattributes.item.lore;

import me.redned.config.ConfigOption;
import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.attribute.AttributeCalculator;
import net.pwing.itemattributes.attribute.ItemAttribute;
import net.pwing.itemattributes.item.AttributableItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class AttributesLoreCreator extends LoreCreator {

    @ConfigOption(name = "header", description = "The header to display before the attributes.")
    private String header;

    @ConfigOption(name = "footer", description = "The footer to display after the attributes.")
    private String footer;

    @ConfigOption(name = "sort-order", description = "The order in which to sort the attributes.")
    private SortOrder sortOrder = SortOrder.ALPHABETICAL;

    @Override
    public List<Component> createLines(AttributableItem item, @Nullable Player viewer) {
        List<Map.Entry<ItemAttribute, Number>> attributes = new ArrayList<>(item.getAttributes().entrySet());
        if (attributes.isEmpty()) {
            return List.of();
        }

        attributes.sort((entry1, entry2) -> this.sortOrder.sorter(entry1.getKey(), entry1.getValue()).compare(entry1.getKey(), entry2.getKey()));

        List<Component> lines = new ArrayList<>();
        if (this.header != null) {
            lines.add(item.renderFromTemplate(this.header));
        }

        for (Map.Entry<ItemAttribute, Number> entry : attributes) {
            ItemAttribute attribute = entry.getKey();
            Number value = entry.getValue();

            Component display = item.render(attribute.getDisplay());
            lines.add(AttributeCalculator.computeOperators(display, value));
        }

        if (this.footer != null) {
            lines.add(item.renderFromTemplate(this.footer));
        }

        return lines;
    }

    public enum SortOrder {
        ALPHABETICAL((attribute, value) -> Comparator.comparing(ItemAttribute::getName)),
        HIGHEST_FIRST((attribute, value) -> Comparator.comparing((ItemAttribute a) -> value.doubleValue()).reversed());

        private final BiFunction<ItemAttribute, Number, Comparator<ItemAttribute>> sorter;

        SortOrder(BiFunction<ItemAttribute, Number, Comparator<ItemAttribute>> sorter) {
            this.sorter = sorter;
        }

        public Comparator<ItemAttribute> sorter(ItemAttribute attribute, Number value) {
            return sorter.apply(attribute, value);
        }
    }
}

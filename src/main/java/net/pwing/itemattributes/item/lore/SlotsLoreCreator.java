package net.pwing.itemattributes.item.lore;

import me.redned.config.ConfigOption;
import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.item.AttributableItem;
import net.pwing.itemattributes.item.slot.SlotHolder;
import net.pwing.itemattributes.message.Messages;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SlotsLoreCreator extends LoreCreator {

    @ConfigOption(name = "header", description = "The header to display before the slots.")
    private String header;

    @ConfigOption(name = "display", description = "The display for rendering slots.", required = true)
    private String display;

    @ConfigOption(name = "footer", description = "The header to display after the slots.")
    private String footer;

    @ConfigOption(name = "displayed-slot-types", description = "The slot types to display.")
    private List<String> displayedSlotTypes;

    @Override
    public List<Component> createLines(AttributableItem item, @Nullable Player viewer) {
        List<SlotHolder> slots = item.getSlots().stream().filter(slot ->
                this.displayedSlotTypes == null || this.displayedSlotTypes.contains(slot.getType().name().toLowerCase(Locale.ROOT))
        ).toList();

        if (slots.isEmpty()) {
            return List.of();
        }

        List<Component> lines = new ArrayList<>();
        if (this.header != null) {
            lines.add(item.renderFromTemplate(this.header));
        }

        for (SlotHolder slot : slots) {
            Component renderedTemplate = item.renderFromTemplate(this.display);
            Component slotInfo;
            if (slot.isEmpty()) {
                slotInfo = viewer == null ? Messages.SLOT_EMPTY.toComponent() : Messages.SLOT_EMPTY.toComponent(viewer);
            } else {
                slotInfo = slot.getSlotInfo(item);
            }

            lines.add(renderedTemplate.replaceText(builder -> builder.matchLiteral("%slot_info%").replacement(slotInfo)));
        }

        if (this.footer != null) {
            lines.add(item.renderFromTemplate(this.footer));
        }

        return lines;
    }
}

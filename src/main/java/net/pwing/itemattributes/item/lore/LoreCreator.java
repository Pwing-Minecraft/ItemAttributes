package net.pwing.itemattributes.item.lore;

import me.redned.config.ConfigOption;
import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.item.AttributableItem;
import net.pwing.itemattributes.requirement.ItemRequirement;
import net.pwing.itemattributes.requirement.config.ItemItemRequirementContextProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public abstract class LoreCreator {
    public static final Map<String, Class<? extends LoreCreator>> LORE_CREATORS = Map.of(
            "simple", SimpleLoreCreator.class,
            "attributes", AttributesLoreCreator.class,
            "slots", SlotsLoreCreator.class,
            "spacer", SpacerLoreCreator.class
    );

    @ConfigOption(name = "requirements", description = "The requirements to apply the lore.", contextProvider = ItemItemRequirementContextProvider.class)
    private List<ItemRequirement<AttributableItem>> requirements;

    public boolean shouldCreateLines(AttributableItem item, Player player) {
        for (ItemRequirement<AttributableItem> requirement : this.getRequirements()) {
            if (!requirement.hasRequirement(item, player)) {
                return false;
            }
        }

        return true;
    }

    public abstract List<Component> createLines(AttributableItem item, @Nullable Player viewer);

    public List<ItemRequirement<AttributableItem>> getRequirements() {
        return this.requirements == null ? List.of() : List.copyOf(this.requirements);
    }
}

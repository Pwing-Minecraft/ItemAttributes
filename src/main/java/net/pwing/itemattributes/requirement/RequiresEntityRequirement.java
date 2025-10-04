package net.pwing.itemattributes.requirement;

import me.redned.config.ConfigOption;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RequiresEntityRequirement implements ItemRequirement<EntityType> {
    @ConfigOption(name = "types", description = "The types of entities that are required to be present.", required = true)
    private List<EntityType> types;

    @Override
    public boolean hasRequirement(EntityType context, @Nullable Player player) {
        return this.types.contains(context);
    }

    @Override
    public ItemRequirementType<EntityType, ? extends ItemRequirement<EntityType>> getType() {
        return ItemRequirementType.REQUIRES_ENTITY;
    }
}

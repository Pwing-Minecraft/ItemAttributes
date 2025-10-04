package net.pwing.itemattributes.requirement.config;

import net.pwing.itemattributes.requirement.RequirementType;
import org.bukkit.entity.EntityType;

public class EntityItemRequirementContextProvider extends ItemRequirementContextProvider<EntityType> {
    public EntityItemRequirementContextProvider() {
        super(RequirementType.ENTITY);
    }
}

package net.pwing.itemattributes.requirement.config;

import net.pwing.itemattributes.item.AttributableItem;
import net.pwing.itemattributes.requirement.RequirementType;

public class ItemItemRequirementContextProvider extends ItemRequirementContextProvider<AttributableItem> {
    public ItemItemRequirementContextProvider() {
        super(RequirementType.ITEM);
    }
}

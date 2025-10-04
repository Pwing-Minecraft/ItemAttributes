package net.pwing.itemattributes.requirement.config;

import net.pwing.itemattributes.attribute.ItemAttribute;
import net.pwing.itemattributes.requirement.RequirementType;

public class AttributeItemRequirementContextProvider extends ItemRequirementContextProvider<ItemAttribute> {
    public AttributeItemRequirementContextProvider() {
        super(RequirementType.ATTRIBUTE);
    }
}

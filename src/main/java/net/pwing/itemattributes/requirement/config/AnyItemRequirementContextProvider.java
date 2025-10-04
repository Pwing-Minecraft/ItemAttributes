package net.pwing.itemattributes.requirement.config;

import net.pwing.itemattributes.requirement.ItemRequirement;
import net.pwing.itemattributes.requirement.ItemRequirementType;

public class AnyItemRequirementContextProvider<T> extends ItemRequirementContextProvider<T> {

    public AnyItemRequirementContextProvider() {
        super(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ItemRequirementType<T, ItemRequirement<T>> getItemRequirementType(String key) {
        for (ItemRequirementType<?, ?> value : ItemRequirementType.values()) {
            if (value.getName().equalsIgnoreCase(key)) {
                return (ItemRequirementType<T, ItemRequirement<T>>) value;
            }
        }

        throw new IllegalArgumentException("No item requirement found for key: " + key);
    }
}

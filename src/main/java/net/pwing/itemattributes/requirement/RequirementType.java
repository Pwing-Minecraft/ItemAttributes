package net.pwing.itemattributes.requirement;

import net.pwing.itemattributes.attribute.ItemAttribute;
import net.pwing.itemattributes.item.AttributableItem;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public final class RequirementType<T> {
    private static final Map<String, RequirementType<?>> TYPES = new HashMap<>();

    public static final RequirementType<ItemAttribute> ATTRIBUTE = register("attribute", ItemAttribute.class);
    public static final RequirementType<EntityType> ENTITY = register("entity", EntityType.class);
    public static final RequirementType<AttributableItem> ITEM = register("item", AttributableItem.class);
    public static final RequirementType<Object> UNIVERSAL = register("universal", Object.class);

    private final String name;
    private final Class<T> type;

    RequirementType(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getType() {
        return this.type;
    }

    public static RequirementType<?> get(String name) {
        RequirementType<?> type = TYPES.get(name);
        if (type == null) {
            throw new IllegalArgumentException("Requirement type with name " + name + " does not exist!");
        }

        return type;
    }

    public static <T> RequirementType<T> register(String name, Class<T> type) {
        if (TYPES.containsKey(name)) {
            throw new IllegalArgumentException("Requirement type with name " + name + " already exists!");
        }

        RequirementType<T> typeInstance = new RequirementType<>(name, type);
        TYPES.put(name, typeInstance);
        return typeInstance;
    }
}

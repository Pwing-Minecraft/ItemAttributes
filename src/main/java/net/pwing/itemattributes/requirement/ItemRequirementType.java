package net.pwing.itemattributes.requirement;

import net.pwing.itemattributes.item.AttributableItem;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ItemRequirementType<T, R extends ItemRequirement<T>> {
    private static final ItemRequirementTable TYPES = new ItemRequirementTable();

    public static final ItemRequirementType<Object, RequiresAttributeRequirement> REQUIRES_ATTRIBUTE = register(RequirementType.UNIVERSAL, "requires_attribute", RequiresAttributeRequirement.class);
    public static final ItemRequirementType<AttributableItem, RequiresComponentRequirement> REQUIRES_COMPONENT = register(RequirementType.ITEM, "requires_component", RequiresComponentRequirement.class);
    public static final ItemRequirementType<EntityType, RequiresEntityRequirement> REQUIRES_ENTITY = register(RequirementType.ENTITY, "requires_entity", RequiresEntityRequirement.class);
    public static final ItemRequirementType<AttributableItem, RequiresItemRequirement> REQUIRES_ITEM = register(RequirementType.ITEM, "requires_item", RequiresItemRequirement.class);

    private final Class<R> requirementType;
    private final String name;
    private final RequirementType<T> type;

    ItemRequirementType(Class<R> requirementType, String name, RequirementType<T> type) {
        this.requirementType = requirementType;
        this.name = name;
        this.type = type;
    }

    public Class<R> getRequirementType() {
        return this.requirementType;
    }

    public String getName() {
        return this.name;
    }

    public RequirementType<T> getType() {
        return this.type;
    }

    @SuppressWarnings("unchecked")
    public static <T, R extends ItemRequirement<T>> ItemRequirementType<T, R> get(RequirementType<T> type, String name) {
        ItemRequirementType<?, ?> itemRequirementType = TYPES.get(type, name);
        if (itemRequirementType == null) {
            return null;
        }

        return (ItemRequirementType<T, R>) itemRequirementType;
    }

    public static <T, R extends ItemRequirement<T>> ItemRequirementType<T, R> register(RequirementType<T> type, String name, Class<R> requirementType) {
        ItemRequirementType<T, R> itemRequirementType = new ItemRequirementType<>(requirementType, name, type);
        TYPES.register(type, name, itemRequirementType);
        return itemRequirementType;
    }

    public static List<ItemRequirementType<?, ?>> values() {
        return TYPES.table.values().stream()
                .flatMap(map -> map.values().stream())
                .toList();
    }

    public static final class ItemRequirementTable {
        private final Map<RequirementType<?>, Map<String, ItemRequirementType<?, ?>>> table = new HashMap<>();

        public <T, R extends ItemRequirement<T>> void register(RequirementType<T> type, String name, ItemRequirementType<T, R> itemRequirementType) {
            if (!this.table.containsKey(type)) {
                this.table.put(type, new HashMap<>());
            }

            Map<String, ItemRequirementType<?, ?>> map = this.table.get(type);
            if (map.containsKey(name)) {
                throw new IllegalArgumentException("Item requirement type with name " + name + " already exists!");
            }

            map.put(name, itemRequirementType);
        }

        public <T, R extends ItemRequirement<T>> ItemRequirementType<T, R> get(RequirementType<T> type, String name) {
            Map<String, ItemRequirementType<?, ?>> map = this.table.get(type);
            if (map == null) {
                return null;
            }

            ItemRequirementType<?, ?> itemRequirementType = map.get(name);
            if (itemRequirementType == null) {
                return null;
            }

            return (ItemRequirementType<T, R>) itemRequirementType;
        }
    }
}

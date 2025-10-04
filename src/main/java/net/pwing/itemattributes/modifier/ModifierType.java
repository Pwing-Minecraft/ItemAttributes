package net.pwing.itemattributes.modifier;

import java.util.HashMap;
import java.util.Map;

public final class ModifierType<T extends ItemModifier> {
    private static final Map<String, ModifierType<?>> TYPES = new HashMap<>();

    public static final ModifierType<AttributeItemModifier> ATTRIBUTE = register("attribute", AttributeItemModifier.class);

    private final String name;
    private final Class<? extends ItemModifier> modifierClass;

    ModifierType(String name, Class<? extends ItemModifier> modifierClass) {
        this.name = name;
        this.modifierClass = modifierClass;
    }

    public String getName() {
        return this.name;
    }

    public Class<? extends ItemModifier> getType() {
        return this.modifierClass;
    }

    public static ModifierType<?> get(String name) {
        ModifierType<?> type = TYPES.get(name);
        if (type == null) {
            throw new IllegalArgumentException("Modifier type with name " + name + " does not exist!");
        }

        return type;
    }

    public static <T extends ItemModifier> ModifierType<T> register(String name, Class<T> modifierClass) {
        if (TYPES.containsKey(name)) {
            throw new IllegalArgumentException("Modifier type with name " + name + " already exists!");
        }

        ModifierType<T> type = new ModifierType<>(name, modifierClass);
        TYPES.put(name, type);
        return type;
    }
}

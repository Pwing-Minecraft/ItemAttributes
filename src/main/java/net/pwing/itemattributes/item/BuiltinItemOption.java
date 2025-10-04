package net.pwing.itemattributes.item;

public enum BuiltinItemOption {
    ALLOW_RENAMING("allow-renaming", boolean.class, true),
    RENDER_VANILLA_ATTRIBUTES("render-vanilla-attributes", boolean.class, false);

    private final String name;
    private final Class<?> type;
    private final Object defaultValue;

    <T> BuiltinItemOption(String name, Class<T> type, T defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getType() {
        return this.type;
    }

    public Object getDefaultValue() {
        return this.defaultValue;
    }
}

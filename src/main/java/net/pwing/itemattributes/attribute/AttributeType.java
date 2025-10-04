package net.pwing.itemattributes.attribute;

import org.bukkit.persistence.PersistentDataType;

import java.util.function.Function;

public enum AttributeType {
    INTEGER(PersistentDataType.INTEGER, Integer::parseInt),
    DOUBLE(PersistentDataType.DOUBLE, Double::parseDouble);

    private final PersistentDataType<?, ?> storageType;
    private final Function<String, ? extends Number> converter;

    <T extends Number> AttributeType(PersistentDataType<?, T> storageType, Function<String, T> converter) {
        this.storageType = storageType;
        this.converter = converter;
    }

    public PersistentDataType<?, ?> getStorageType() {
        return this.storageType;
    }

    @SuppressWarnings("unchecked")
    public <T> T convert(Number number) {
        PersistentDataType<?, T> type = (PersistentDataType<?, T>) this.storageType;
        if (type == PersistentDataType.INTEGER) {
            return type.getComplexType().cast(number.intValue());
        } else if (type == PersistentDataType.DOUBLE) {
            return type.getComplexType().cast(number.doubleValue());
        } else {
            throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Number> T convert(String string) {
        return (T) this.converter.apply(string);
    }
}

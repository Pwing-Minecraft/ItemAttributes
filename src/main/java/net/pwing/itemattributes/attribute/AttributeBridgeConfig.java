package net.pwing.itemattributes.attribute;

import com.google.common.base.Preconditions;
import me.redned.config.ConfigOption;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

public class AttributeBridgeConfig {

    @ConfigOption(name = "type", description = "The bridge type.", required = true)
    private AttributeBridgeType type;

    @ConfigOption(name = "key", description = "The key of the bridge.", required = true)
    private NamespacedKey key;

    @ConfigOption(name = "operation", description = "The operation.")
    private AttributeOperation operation;

    @ConfigOption(name = "modifier-expression", description = "A modifier expression for the value.")
    private String modifierExpression;

    @ConfigOption(name = "event", description = "The event this bridge applies to.")
    private String event;

    public AttributeBridgeType getType() {
        return this.type;
    }

    public NamespacedKey getKey() {
        return this.key;
    }

    public AttributeOperation getOperation() {
        return this.operation;
    }

    @Nullable
    public String getModifierExpression() {
        return this.modifierExpression;
    }

    @Nullable
    public String getEvent() {
        return this.event;
    }

    public AttributeApplicator toApplicator(ItemAttribute attribute) {
        return AttributeApplicator.builder()
                .attribute(attribute)
                .type(this.type)
                .key(this.key)
                .operation(this.operation)
                .modifierExpression(this.modifierExpression)
                .event(this.event)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private AttributeBridgeType type;
        private NamespacedKey key;
        private AttributeOperation operation;
        private String modifierExpression;
        private String event;

        public Builder type(AttributeBridgeType type) {
            this.type = type;
            return this;
        }

        public Builder key(NamespacedKey key) {
            this.key = key;
            return this;
        }

        public Builder operation(AttributeOperation operation) {
            this.operation = operation;
            return this;
        }

        public Builder modifierExpression(String modifierExpression) {
            this.modifierExpression = modifierExpression;
            return this;
        }

        public Builder event(String event) {
            this.event = event;
            return this;
        }

        public AttributeBridgeConfig build() {
            Preconditions.checkNotNull(this.type, "The type cannot be null.");
            Preconditions.checkNotNull(this.key, "The key cannot be null.");

            AttributeBridgeConfig config = new AttributeBridgeConfig();
            config.type = this.type;
            config.key = this.key;
            config.operation = this.operation;
            config.modifierExpression = this.modifierExpression;
            config.event = this.event;
            return config;
        }
    }
}


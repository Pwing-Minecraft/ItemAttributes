package net.pwing.itemattributes.attribute;

import com.google.common.base.Preconditions;
import me.redned.config.ConfigOption;
import me.redned.config.Scoped;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class AttributeApplicator {
    @Scoped
    private ItemAttribute attribute;

    @ConfigOption(name = "type", description = "The bridge type of the attribute.", required = true)
    private AttributeBridgeType type;

    @ConfigOption(name = "key", description = "The key of the attribute.", required = true)
    private NamespacedKey key;

    @ConfigOption(name = "operation", description = "The operation of the attribute.", required = true)
    private AttributeOperation operation;

    @ConfigOption(name = "modifier-expression", description = "A modifier for handling the attribute value.")
    private String modifierExpression;

    @ConfigOption(name = "event", description = "The event to apply the attribute on.")
    private String event;

    public ItemAttribute getAttribute() {
        return this.attribute;
    }

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

    void apply(Player player, Number value) {
        this.getType().getBridge().apply(player, this, value);
    }

    void reset(Player player) {
        this.getType().getBridge().reset(player, this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ItemAttribute attribute;
        private AttributeBridgeType type;
        private NamespacedKey key;
        private AttributeOperation operation;
        private String modifierExpression;
        private String event;

        public Builder attribute(ItemAttribute attribute) {
            this.attribute = attribute;
            return this;
        }

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

        public AttributeApplicator build() {
            Preconditions.checkNotNull(this.attribute, "The attribute cannot be null.");
            Preconditions.checkNotNull(this.type, "The type cannot be null.");
            Preconditions.checkNotNull(this.key, "The key cannot be null.");

            AttributeApplicator applicator = new AttributeApplicator();
            applicator.attribute = this.attribute;
            applicator.type = this.type;
            applicator.key = this.key;
            applicator.operation = this.operation;
            applicator.modifierExpression = this.modifierExpression;
            applicator.event = this.event;
            return applicator;
        }
    }
}

package net.pwing.itemattributes.attribute;

import com.google.common.base.Preconditions;
import me.redned.config.ConfigOption;
import me.redned.config.PostProcessable;
import me.redned.config.Scoped;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class AttributeApplicator implements PostProcessable {
    @Scoped
    private ItemAttribute attribute;

    @ConfigOption(name = "type", description = "The bridge type of the attribute.", required = true)
    private AttributeBridgeType type;

    @ConfigOption(name = "key", description = "The key of the attribute.", required = true)
    private NamespacedKey key;

    @ConfigOption(name = "operation", description = "The operation of the attribute.")
    private AttributeOperation operation;

    @ConfigOption(name = "modifier-expression", description = "A modifier for handling the attribute value.")
    private String modifierExpression;

    @ConfigOption(name = "targeted-modifier-expression", description = "Named modifier expressions for targeted event outputs.")
    private Map<String, String> targetedModifierExpressions;

    @ConfigOption(name = "event", description = "The event to apply the attribute on.")
    private String event;

    @ConfigOption(name = "cancel-event", description = "Whether to cancel the event this is applied to.")
    private boolean cancelEvent;

    @ConfigOption(name = "source", description = "The source bridge config for redirect attributes.")
    private AttributeBridgeConfig sourceConfig;

    @ConfigOption(name = "destination", description = "The destination bridge config for redirect attributes.")
    private AttributeBridgeConfig destinationConfig;

    @ConfigOption(name = "destination-entity", description = "Who a redirect attribute should apply to: SELF or TARGET.")
    private RedirectDestination destinationEntity = RedirectDestination.SELF;

    @ConfigOption(name = "success-event", description = "Optional event fired when a redirect successfully activates on its destination.")
    private String successEvent;

    private AttributeApplicator source;
    private AttributeApplicator destination;

    @Override
    public void postProcess() {
        if (this.sourceConfig != null) {
            this.source = this.sourceConfig.toApplicator(this.attribute);
        }

        if (this.destinationConfig != null) {
            this.destination = this.destinationConfig.toApplicator(this.attribute);
        }
    }

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
        if (this.operation != null) {
            return this.operation;
        }

        return this.source == null ? null : this.source.getOperation();
    }

    @Nullable
    public String getModifierExpression() {
        if (this.modifierExpression != null) {
            return this.modifierExpression;
        }

        return this.source == null ? null : this.source.getModifierExpression();
    }

    public Map<String, String> getTargetedModifierExpressions() {
        if (this.targetedModifierExpressions != null) {
            return Map.copyOf(this.targetedModifierExpressions);
        }

        return this.source == null ? Map.of() : this.source.getTargetedModifierExpressions();
    }

    @Nullable
    public String getEvent() {
        if (this.event != null) {
            return this.event;
        }

        return this.source == null ? null : this.source.getEvent();
    }

    public boolean isCancelEvent() {
        return this.cancelEvent;
    }

    public RedirectDestination getDestinationEntity() {
        return this.destinationEntity;
    }

    @Nullable
    public String getSuccessEvent() {
        return this.successEvent;
    }

    @Nullable
    public AttributeApplicator getSource() {
        return this.source;
    }

    @Nullable
    public AttributeApplicator getDestination() {
        return this.destination;
    }

    public AttributeApplicator getSourceOrSelf() {
        if (this.source == null) {
            return this;
        }

        return this.source;
    }

    @Nullable
    public AttributeApplicator getResolvedDestination() {
        if (this.destination == null) {
            return null;
        }

        return this.destination;
    }

    void apply(Player player, Number value) {
        this.getType().apply(player, this, value);
    }

    void reset(Player player) {
        this.getType().reset(player, this);
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
        private boolean cancelEvent;
        private AttributeBridgeConfig sourceConfig;
        private AttributeBridgeConfig destinationConfig;
        private RedirectDestination destinationEntity = RedirectDestination.SELF;
        private String successEvent;

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

        public Builder cancelEvent(boolean cancelEvent) {
            this.cancelEvent = cancelEvent;
            return this;
        }

        public Builder sourceConfig(AttributeBridgeConfig sourceConfig) {
            this.sourceConfig = sourceConfig;
            return this;
        }

        public Builder destinationConfig(AttributeBridgeConfig destinationConfig) {
            this.destinationConfig = destinationConfig;
            return this;
        }

        public Builder destinationEntity(RedirectDestination destinationEntity) {
            this.destinationEntity = destinationEntity;
            return this;
        }

        public Builder successEvent(String successEvent) {
            this.successEvent = successEvent;
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
            applicator.cancelEvent = this.cancelEvent;
            applicator.sourceConfig = this.sourceConfig;
            applicator.destinationConfig = this.destinationConfig;
            applicator.destinationEntity = this.destinationEntity;
            applicator.successEvent = this.successEvent;
            applicator.postProcess();
            return applicator;
        }
    }
}

package net.pwing.itemattributes.requirement;

import com.google.common.base.Preconditions;
import me.redned.config.ConfigOption;
import net.pwing.itemattributes.attribute.AttributeBridgeType;
import net.pwing.itemattributes.attribute.AttributePredicate;
import net.pwing.itemattributes.util.IntRange;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class RequiresAttributeRequirement implements ItemRequirement<Object> {

    @ConfigOption(name = "type", description = "The type of attribute to require", required = true)
    private AttributeBridgeType type;

    @ConfigOption(name = "key", description = "The key of the attribute to require", required = true)
    private NamespacedKey key;

    @ConfigOption(name = "predicate", description = "The predicate to run the attribute value against.", required = true)
    private AttributePredicate predicate;

    @ConfigOption(name = "range", description = "The range to run the probability against.")
    private IntRange range;

    @ConfigOption(name = "value", description = "The value to run the probability against.")
    private double value;

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean hasRequirement(Object context, @Nullable Player player) {
        if (player == null) {
            return false;
        }

        Number value = this.type.getValue(player, this.key);
        if (this.predicate == AttributePredicate.PROBABILITY && this.range != null) {
            double min = this.range.getMin();
            double max = this.range.getMax();
            double random = ThreadLocalRandom.current().nextDouble(min, max);
            return value.doubleValue() >= random;
        } else if (this.predicate == AttributePredicate.IN_RANGE && this.range != null) {
            double min = this.range.getMin();
            double max = this.range.getMax();
            return value.doubleValue() >= min && value.doubleValue() <= max;
        } else if (this.predicate == AttributePredicate.GREATER_THAN && this.value != 0) {
            return value.doubleValue() > this.value;
        } else if (this.predicate == AttributePredicate.LESS_THAN && this.value != 0) {
            return value.doubleValue() < this.value;
        } else if (this.predicate == AttributePredicate.EQUALS && this.value != 0) {
            return value.doubleValue() == this.value;
        }

        return false;
    }

    @Override
    public ItemRequirementType<Object, ? extends ItemRequirement<Object>> getType() {
        return ItemRequirementType.REQUIRES_ATTRIBUTE;
    }

    public static class Builder {
        private AttributeBridgeType type;
        private NamespacedKey key;
        private AttributePredicate predicate;
        private IntRange range;
        private double value;

        public Builder type(AttributeBridgeType type) {
            this.type = type;
            return this;
        }

        public Builder key(NamespacedKey key) {
            this.key = key;
            return this;
        }

        public Builder predicate(AttributePredicate predicate) {
            this.predicate = predicate;
            return this;
        }

        public Builder range(IntRange range) {
            this.range = range;
            return this;
        }

        public Builder value(double value) {
            this.value = value;
            return this;
        }

        public RequiresAttributeRequirement build() {
            Preconditions.checkNotNull(this.type, "The type of the attribute requirement is null.");
            Preconditions.checkNotNull(this.key, "The key of the attribute requirement is null.");
            Preconditions.checkNotNull(this.predicate, "The predicate of the attribute requirement is null.");

            RequiresAttributeRequirement requirement = new RequiresAttributeRequirement();
            requirement.type = type;
            requirement.key = this.key;
            requirement.predicate = this.predicate;
            requirement.range = this.range;
            requirement.value = this.value;
            return requirement;
        }
    }
}

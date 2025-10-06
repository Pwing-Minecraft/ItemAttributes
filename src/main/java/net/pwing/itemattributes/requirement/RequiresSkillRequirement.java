package net.pwing.itemattributes.requirement;

import me.redned.config.ConfigOption;
import net.pwing.itemattributes.attribute.AttributePredicate;
import net.pwing.itemattributes.feature.skill.Skills;
import net.pwing.itemattributes.util.IntRange;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class RequiresSkillRequirement implements ItemRequirement<Object> {

    @ConfigOption(name = "key", description = "The key of the skill to require", required = true)
    private NamespacedKey key;

    @ConfigOption(name = "predicate", description = "The predicate to run the skill value against.", required = true)
    private AttributePredicate predicate;

    @ConfigOption(name = "range", description = "The range to run the probability against.")
    private IntRange range;

    @ConfigOption(name = "value", description = "The value to run the probability against.")
    private double value;
    
    @Override
    public boolean hasRequirement(Object context, @Nullable Player player) {
        if (player == null) {
            return false;
        }

        double value = Skills.getLevel(player, this.key);
        if (this.predicate == AttributePredicate.PROBABILITY && this.range != null) {
            double min = this.range.getMin();
            double max = this.range.getMax();
            double random = ThreadLocalRandom.current().nextDouble(min, max);
            return value >= random;
        } else if (this.predicate == AttributePredicate.IN_RANGE && this.range != null) {
            double min = this.range.getMin();
            double max = this.range.getMax();
            return value >= min && value <= max;
        } else if (this.predicate == AttributePredicate.GREATER_THAN && this.value != 0) {
            return value > this.value;
        } else if (this.predicate == AttributePredicate.LESS_THAN && this.value != 0) {
            return value < this.value;
        } else if (this.predicate == AttributePredicate.EQUALS && this.value != 0) {
            return value == this.value;
        }

        return false;
    }

    @Override
    public ItemRequirementType<Object, ? extends ItemRequirement<Object>> getType() {
        return ItemRequirementType.REQUIRES_SKILL;
    }
}

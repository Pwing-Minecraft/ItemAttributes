package net.pwing.itemattributes.requirement;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface ItemRequirement<C> {

    boolean hasRequirement(C context, @Nullable Player player);

    ItemRequirementType<C, ? extends ItemRequirement<C>> getType();

    default RequirementType<C> getRequirementType() {
        return getType().getType();
    }
}

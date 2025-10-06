package net.pwing.itemattributes.feature.skill;

import net.pwing.itemattributes.feature.FeatureInstance;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface SkillFeature extends FeatureInstance {

    boolean hasSkill(Player player, NamespacedKey skillKey, int level);

    @Nullable
    SkillHolder getSkill(NamespacedKey skillKey);

    double getLevel(Player player, NamespacedKey skillKey);
}

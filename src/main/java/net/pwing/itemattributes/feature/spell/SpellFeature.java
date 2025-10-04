package net.pwing.itemattributes.feature.spell;

import net.pwing.itemattributes.feature.FeatureInstance;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface SpellFeature extends FeatureInstance {

    void cast(Player player, NamespacedKey spellKey);

    @Nullable
    SpellHolder getSpell(NamespacedKey spellKey);
}

package net.pwing.itemattributes.feature.spell;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public interface SpellHolder {

    NamespacedKey getKey();

    String getName();

    Component getDisplayName();

    void cast(Player player);
}

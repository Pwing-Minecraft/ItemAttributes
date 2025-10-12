package net.pwing.itemattributes.feature.spell;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.util.SpellData;
import net.kyori.adventure.text.Component;
import net.pwing.itemattributes.feature.PluginFeature;
import net.pwing.itemattributes.message.TextUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class MagicSpellsSpellFeature extends PluginFeature<SpellFeature> implements SpellFeature {

    public MagicSpellsSpellFeature() {
        super("MagicSpells");
    }

    @Override
    public void cast(Player player, NamespacedKey spellKey) {
        Spell spell = MagicSpells.getSpellByInternalName(spellKey.getKey());
        if (spell == null) {
            return;
        }

        spell.hardCast(new SpellData(player, 1.0F, null));
    }

    @Nullable
    @Override
    public SpellHolder getSpell(NamespacedKey spellKey) {
        Spell spell = MagicSpells.getSpellByInternalName(spellKey.getKey());
        if (spell == null) {
            return null;
        }

        return new MagicSpellsSpellHolder(spell);
    }

    public record MagicSpellsSpellHolder(Spell spell) implements SpellHolder {

        @Override
        public NamespacedKey getKey() {
            return new NamespacedKey("magicspells", this.spell.getInternalName());
        }

        @Override
        public String getName() {
            return this.spell.getName();
        }

        @Override
        public Component getDisplayName() {
            return TextUtils.fromMiniMessage(this.getName());
        }

        @Override
        public void cast(Player player) {
            this.spell.hardCast(new SpellData(player, 1.0F, null));
        }
    }
}

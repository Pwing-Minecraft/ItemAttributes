package net.pwing.itemattributes.feature.spell;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.elmakers.mine.bukkit.api.spell.SpellTemplate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.pwing.itemattributes.feature.PluginFeature;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class MagicSpellFeature extends PluginFeature<SpellFeature> implements SpellFeature {
    private final MagicAPI magicAPI;

    public MagicSpellFeature() {
        super("Magic");

        //noinspection PatternVariableHidesField
        if (!(this.getPlugin() instanceof MagicAPI magicAPI)) {
            throw new IllegalStateException("MagicAPI not found!");
        }

        this.magicAPI = magicAPI;
    }

    @Override
    public void cast(Player player, NamespacedKey spellKey) {
        this.magicAPI.cast(spellKey.getKey(), new String[0]);
    }

    @Nullable
    @Override
    public SpellHolder getSpell(NamespacedKey spellKey) {
        SpellTemplate template = this.magicAPI.getSpellTemplate(spellKey.getKey());
        return new MagicSpellHolder(
                template,
                player -> this.cast(player, spellKey)
        );
    }

    public record MagicSpellHolder(SpellTemplate template, Consumer<Player> castConsumer) implements SpellHolder {

        @Override
        public NamespacedKey getKey() {
            return new NamespacedKey("magic", this.template.getKey());
        }

        @Override
        public String getName() {
            return this.template.getName();
        }

        @Override
        public Component getDisplayName() {
            return LegacyComponentSerializer.legacySection().deserialize(this.getName());
        }

        @Override
        public void cast(Player player) {
            this.castConsumer.accept(player);
        }
    }
}

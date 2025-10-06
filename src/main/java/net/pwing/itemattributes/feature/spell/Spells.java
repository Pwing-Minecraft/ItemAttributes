package net.pwing.itemattributes.feature.spell;

import net.pwing.itemattributes.feature.FeatureController;
import net.pwing.itemattributes.feature.PluginFeature;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class Spells extends FeatureController<PluginFeature<SpellFeature>> {

    public static void init() {
        if (Bukkit.getPluginManager().isPluginEnabled("MagicSpells")) {
            register(new MagicSpellsSpellFeature());
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Magic")) {
            register(new MagicSpellFeature());
        }
    }

    public static void cast(Player player, NamespacedKey spellKey) {
        feature(spellKey).ifPresent(feature -> feature.cast(player, spellKey));
    }

    @Nullable
    public static SpellHolder getSpell(NamespacedKey spellKey) {
        return feature(spellKey).map(feature -> feature.getSpell(spellKey)).orElse(null);
    }

    public static <T extends PluginFeature<SpellFeature> & SpellFeature> void register(T feature) {
        registerFeature(SpellFeature.class, feature);
    }

    private static Optional<SpellFeature> feature(NamespacedKey key) {
        return Optional.ofNullable(getFeature(key));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static <T extends PluginFeature<SpellFeature> & SpellFeature> SpellFeature getFeature(NamespacedKey key) {
        List<T> features = (List) getFeatures(SpellFeature.class);
        for (T feature : features) {
            if (!feature.isEnabled()) {
                continue;
            }

            String pluginNamespace = feature.getPlugin().getName().toLowerCase(Locale.ROOT);
            if (key.getNamespace().equals(pluginNamespace)) {
                return feature;
            }
        }

        return null;
    }
}

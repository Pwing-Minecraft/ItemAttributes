package net.pwing.itemattributes.feature.attribute;

import net.pwing.itemattributes.attribute.AttributeApplicator;
import net.pwing.itemattributes.feature.FeatureController;
import net.pwing.itemattributes.feature.PluginFeature;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class CustomAttributes extends FeatureController<PluginFeature<AttributesFeature>> {

    public static void init() {
        if (Bukkit.getPluginManager().isPluginEnabled("MagicSpells")) {
            register(new MagicSpellsAttributeFeature());
        }

        if (Bukkit.getPluginManager().isPluginEnabled("AuraSkills")) {
            register(new AuraSkillsAttributeFeature());
        }

    }

    public static Number getValue(Player player, NamespacedKey attributeKey) {
        return feature(attributeKey).map(feature -> feature.getValue(player, attributeKey)).orElse(0);
    }

    public static void apply(Player player, AttributeApplicator applicator, Number value) {
        feature(applicator.getKey()).ifPresent(feature -> feature.apply(player, applicator, value));
    }

    public static void reset(Player player, NamespacedKey attributeKey) {
        feature(attributeKey).ifPresent(feature -> feature.reset(player, attributeKey));
    }

    public static <T extends PluginFeature<AttributesFeature> & AttributesFeature> void register(T feature) {
        registerFeature(AttributesFeature.class, feature);
    }

    private static Optional<AttributesFeature> feature(NamespacedKey key) {
        return Optional.ofNullable(getFeature(key));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static <T extends PluginFeature<AttributesFeature> & AttributesFeature> AttributesFeature getFeature(NamespacedKey key) {
        List<T> features = (List) getFeatures(AttributesFeature.class);
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

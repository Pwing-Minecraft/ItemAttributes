package net.pwing.itemattributes.feature.skill;

import net.pwing.itemattributes.feature.FeatureController;
import net.pwing.itemattributes.feature.PluginFeature;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class Skills extends FeatureController<PluginFeature<SkillFeature>> {

    public static void init() {
        if (Bukkit.getPluginManager().isPluginEnabled("AuraSkills")) {
            register(new AuraSkillsSkillFeature());
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Fabled")) {
            register(new FabledSkillFeature());
        }
    }

    public static boolean hasSkill(Player player, NamespacedKey skillKey, int level) {
        return feature(skillKey).map(feature -> feature.hasSkill(player, skillKey, level)).orElse(false);
    }

    @Nullable
    public static SkillHolder getSkill(NamespacedKey skillKey) {
        return feature(skillKey).map(feature -> feature.getSkill(skillKey)).orElse(null);
    }

    public static double getLevel(Player player, NamespacedKey skillKey) {
        return feature(skillKey).map(feature -> feature.getLevel(player, skillKey)).orElse(0.0);
    }

    public static <T extends PluginFeature<SkillFeature> & SkillFeature> void register(T feature) {
        registerFeature(SkillFeature.class, feature);
    }

    private static Optional<SkillFeature> feature(NamespacedKey key) {
        return Optional.ofNullable(getFeature(key));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static <T extends PluginFeature<SkillFeature> & SkillFeature> SkillFeature getFeature(NamespacedKey key) {
        List<T> features = (List) getFeatures(SkillFeature.class);
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

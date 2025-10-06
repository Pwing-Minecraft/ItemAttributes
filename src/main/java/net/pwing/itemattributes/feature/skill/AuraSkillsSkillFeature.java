package net.pwing.itemattributes.feature.skill;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.user.SkillsUser;
import net.pwing.itemattributes.feature.PluginFeature;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class AuraSkillsSkillFeature extends PluginFeature<SkillFeature> implements SkillFeature {

    public AuraSkillsSkillFeature() {
        super("AuraSkills");
    }

    @Override
    public boolean hasSkill(Player player, NamespacedKey skillKey, int level) {
        Skill skill = AuraSkillsApi.get().getGlobalRegistry().getSkill(NamespacedId.of(NamespacedId.AURASKILLS, skillKey.getKey()));
        if (skill == null) {
            return false;
        }

        SkillsUser user = AuraSkillsApi.get().getUser(player.getUniqueId());
        return user.getSkillLevel(skill) >= level;
    }

    @Nullable
    @Override
    public SkillHolder getSkill(NamespacedKey skillKey) {
        Skill skill = AuraSkillsApi.get().getGlobalRegistry().getSkill(NamespacedId.of(NamespacedId.AURASKILLS, skillKey.getKey()));
        if (skill != null) {
            return new AuraSkillHolder(skill);
        }

        return null;
    }

    @Override
    public double getLevel(Player player, NamespacedKey skillKey) {
        Skill skill = AuraSkillsApi.get().getGlobalRegistry().getSkill(NamespacedId.of(NamespacedId.AURASKILLS, skillKey.getKey()));
        if (skill == null) {
            return 0;
        }

        SkillsUser user = AuraSkillsApi.get().getUser(player.getUniqueId());
        return user.getSkillLevel(skill);
    }

    public record AuraSkillHolder(Skill skill) implements SkillHolder {

        @Override
        public NamespacedKey getKey() {
            return new NamespacedKey(NamespacedId.AURASKILLS, this.skill.getId().getKey());
        }

        @Override
        public String getName() {
            return this.skill.getDisplayName(AuraSkillsApi.get().getMessageManager().getDefaultLanguage());
        }
    }
}

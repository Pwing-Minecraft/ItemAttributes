package net.pwing.itemattributes.feature.skill;

import net.pwing.itemattributes.feature.PluginFeature;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.skills.Skill;

public class FabledSkillFeature extends PluginFeature<SkillFeature> implements SkillFeature {

    public FabledSkillFeature() {
        super("Fabled");
    }

    @Override
    public boolean hasSkill(Player player, NamespacedKey skillKey, int level) {
        PlayerData data = Fabled.getData(player);
        if (data == null) {
            return false;
        }

        return data.getSkillLevel(skillKey.getKey()) >= level;
    }

    @Nullable
    @Override
    public SkillHolder getSkill(NamespacedKey skillKey) {
        Skill skill = Fabled.getSkill(skillKey.getKey());
        if (skill != null) {
            return new FabledSkillHolder(skill);
        }

        return null;
    }

    @Override
    public double getLevel(Player player, NamespacedKey skillKey) {
        PlayerData data = Fabled.getData(player);
        if (data == null) {
            return 0;
        }

        return data.getSkillLevel(skillKey.getKey());
    }

    public record FabledSkillHolder(Skill skill) implements SkillHolder {

        @Override
        public NamespacedKey getKey() {
            return new NamespacedKey("fabled", this.skill.getKey());
        }

        @Override
        public String getName() {
            return this.skill.getName();
        }
    }
}

package net.pwing.itemattributes.feature.entities;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.MobExecutor;
import net.pwing.itemattributes.feature.PluginFeature;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;

public class MythicMobsFeature extends PluginFeature<EntitiesFeature> implements EntitiesFeature {

    public MythicMobsFeature() {
        super("MythicMobs");
    }

    @Override
    public boolean isEntityType(NamespacedKey identifier, Entity entity) {
        MobExecutor manager = MythicBukkit.inst().getMobManager();
        if (!manager.isMythicMob(entity)) {
            return false;
        }

        return manager.getMythicMob(identifier.getKey())
                .map(mob -> manager.getMythicMobInstance(entity).getType().equals(mob))
                .orElse(false);
    }
}

package net.pwing.itemattributes.feature.entities;

import net.pwing.itemattributes.feature.FeatureInstance;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;

public interface EntitiesFeature extends FeatureInstance {

    boolean isEntityType(NamespacedKey identifier, Entity entity);
}

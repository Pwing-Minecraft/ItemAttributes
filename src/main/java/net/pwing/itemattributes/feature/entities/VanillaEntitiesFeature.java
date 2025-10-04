package net.pwing.itemattributes.feature.entities;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

class VanillaEntitiesFeature implements EntitiesFeature {
    static final VanillaEntitiesFeature INSTANCE = new VanillaEntitiesFeature();

    private VanillaEntitiesFeature() {
    }

    @Override
    public boolean isEntityType(NamespacedKey identifier, Entity entity) {
        String type = identifier.getKey();

        EntityType entityType = Registry.ENTITY_TYPE.get(NamespacedKey.minecraft(type));
        if (entityType == null) {
            return false;
        }

        return entity.getType() == entityType;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

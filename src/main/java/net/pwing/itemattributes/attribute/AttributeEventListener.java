package net.pwing.itemattributes.attribute;

import net.pwing.itemattributes.ItemAttributes;
import net.pwing.itemattributes.requirement.RequirementType;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class AttributeEventListener implements Listener {
    private final AttributeManager manager;

    public AttributeEventListener(AttributeManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            Number value = AttributeCalculator.calculateFullAttributeValueForEvent(player, this.manager, "entity_damage", new AttributeRequirement<>(RequirementType.ENTITY, event.getEntity().getType()));
            if (value.doubleValue() != 0) {
                event.setDamage(Math.max(0, event.getDamage() + value.doubleValue()));
            }
        }

        if (event.getEntity() instanceof Player player) {
            Number value = AttributeCalculator.calculateFullAttributeValueForEvent(player, this.manager, "entity_take_damage", new AttributeRequirement<>(RequirementType.ENTITY, event.getDamager().getType()));
            if (value.doubleValue() != 0) {
                event.setDamage(Math.max(0, event.getDamage() + value.doubleValue()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile.hasMetadata("attribute_launched")) {
            return;
        }

        if (!(projectile.getShooter() instanceof Player shooter)) {
            return;
        }

        int value = AttributeCalculator.calculateFullAttributeValueForEvent(shooter, this.manager, "projectile_launch", new AttributeRequirement<>(RequirementType.ENTITY, projectile.getType())).intValue();
        if (value <= 0) {
            if (value < 0) {
                event.setCancelled(true);
            }

            return;
        }

        // TODO: Add a condition for the projectile type

        Vector originalVelocity = projectile.getVelocity();
        for (int i = 0; i < value; i++) {
            Projectile newProjectile = (Projectile) projectile.copy();
            newProjectile.teleport(projectile.getLocation());

            newProjectile.setMetadata("attribute_launched", new FixedMetadataValue(ItemAttributes.getInstance(), true));
            newProjectile.setShooter(shooter);

            // Slight randomization to prevent them all being in the exact same path
            Vector randomizedVelocity = originalVelocity.clone().add(new Vector(
                    (Math.random() - 0.5) * 0.2, // small horizontal variance
                    (Math.random() - 0.5) * 0.2,
                    (Math.random() - 0.5) * 0.2
            ));

            newProjectile.setVelocity(randomizedVelocity);
            newProjectile.getWorld().addEntity(newProjectile);

            if (newProjectile instanceof AbstractArrow arrow) {
                arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRegainHealth(EntityRegainHealthEvent event) {
        if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED || event.getAmount() <= 0.0D) {
            return;
        }

        if (event.getEntity() instanceof Player player) {
            Number value = AttributeCalculator.calculateFullAttributeValueForEvent(player, this.manager, "regen");
            if (value.doubleValue() != 0) {
                event.setAmount(Math.max(0, event.getAmount() + value.doubleValue()));
            }
        }
    }
}

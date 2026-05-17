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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

public class AttributeEventListener implements Listener {
    private final AttributeManager manager;

    public AttributeEventListener(AttributeManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
            AttributeCalculator.applyTrackedFireTickSuccessEvents(this.manager, event);
        }

        // Can happen from other plugins doing stupid stuff
        if (event.getCause() == null) {
            return;
        }

        String cause = switch (event.getCause()) {
            case PROJECTILE -> "projectile";
            case SUFFOCATION -> "suffocation";
            case FALL -> "fall";
            case FIRE, FIRE_TICK -> "fire";
            case LAVA -> "lava";
            case DROWNING -> "drowning";
            case ENTITY_EXPLOSION, BLOCK_EXPLOSION -> "explosion";
            case FREEZE -> "freeze";
            case CAMPFIRE, HOT_FLOOR -> "burn";
            case STARVATION -> "starvation";
            default -> null;
        };

        if (cause == null) {
            return;
        }

        if (event.getEntity() instanceof Player player) {
            AttributeCalculator.EventAttributeResult result = AttributeCalculator.calculateEventAttributeResult(player, this.manager, cause + "_damage", Map.of("damage", event.getFinalDamage()));
            double nonRedirectTotal = AttributeCalculator.getNonRedirectTotal(result);
            if (nonRedirectTotal != 0) {
                double rawValue = event.getDamage() + nonRedirectTotal;
                if (rawValue < 0 && AttributeCalculator.shouldNonRedirectCancel(result)) {
                    event.setCancelled(true);
                }

                event.setDamage(Math.max(0, rawValue));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            AttributeRequirement<?> entityRequirement = new AttributeRequirement<>(RequirementType.ENTITY, event.getEntity().getType());
            AttributeCalculator.EventAttributeResult result = AttributeCalculator.calculateEventAttributeResult(player, this.manager, "entity_damage", Map.of("damage", event.getFinalDamage()), entityRequirement);
            double nonRedirectTotal = AttributeCalculator.getNonRedirectTotal(result);
            if (nonRedirectTotal != 0) {
                double rawValue = event.getDamage() + nonRedirectTotal;
                if (rawValue < 0 && AttributeCalculator.shouldNonRedirectCancel(result)) {
                    event.setCancelled(true);
                }

                event.setDamage(Math.max(0, rawValue));
            }

            List<AttributeCalculator.RedirectApplication> redirectApplications = AttributeCalculator.applyRedirectAttributes(this.manager, player, event.getEntity(), "entity_damage", Map.of("damage", event.getFinalDamage()), entityRequirement);
            AttributeCalculator.applyRedirectSuccessEventBonuses(this.manager, player, event, event.getEntity(), entityRequirement, redirectApplications);
        }

        if (event.getEntity() instanceof Player player) {
            AttributeRequirement<?> entityRequirement = new AttributeRequirement<>(RequirementType.ENTITY, event.getDamager().getType());
            AttributeCalculator.EventAttributeResult result = AttributeCalculator.calculateEventAttributeResult(player, this.manager, "entity_take_damage", Map.of("damage", event.getFinalDamage()), entityRequirement);
            double nonRedirectTotal = AttributeCalculator.getNonRedirectTotal(result);
            if (nonRedirectTotal != 0) {
                double rawValue = event.getDamage() + nonRedirectTotal;
                if (rawValue < 0 && AttributeCalculator.shouldNonRedirectCancel(result)) {
                    event.setCancelled(true);
                }

                event.setDamage(Math.max(0, rawValue));
            }

            AttributeCalculator.applyRedirectAttributes(this.manager, player, event.getDamager(), "entity_take_damage", Map.of("damage", event.getFinalDamage()), entityRequirement);
        }
    }

    @EventHandler
    public void onProjectileUse(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player player) {
            AttributeRequirement<?> entityRequirement = new AttributeRequirement<>(RequirementType.ENTITY, event.getProjectile().getType());
            AttributeCalculator.EventAttributeResult result = AttributeCalculator.calculateEventAttributeResult(player, this.manager, "arrow_shoot", entityRequirement);
            double chanceToCancelArrowConsume = AttributeCalculator.getNonRedirectTotal(result) / 100.0D;
            if (chanceToCancelArrowConsume != 0) {
                if (Math.random() <= chanceToCancelArrowConsume) {
                    ((Player) event.getEntity()).getInventory().addItem(event.getConsumable());
                    if (event.getProjectile() instanceof AbstractArrow arrow) {
                        arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                    }
                }
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

        AttributeCalculator.EventAttributeResult result = AttributeCalculator.calculateEventAttributeResult(shooter, this.manager, "projectile_launch", new AttributeRequirement<>(RequirementType.ENTITY, projectile.getType()));
        AttributeCalculator.applyProjectileLaunchTargets(projectile, result);

        int value = (int) AttributeCalculator.getNonRedirectTotal(result);
        if (value <= 0) {
            if (/*result.shouldCancelEvent() &&*/ value < 0) { // Have to cancel here
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

            AttributeCalculator.applyProjectileLaunchTargets(newProjectile, result);

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
            AttributeCalculator.EventAttributeResult result = AttributeCalculator.calculateEventAttributeResult(player, this.manager, "regen");
            double nonRedirectTotal = AttributeCalculator.getNonRedirectTotal(result);
            double rawValue = event.getAmount() + nonRedirectTotal;
            if (rawValue < 0 && AttributeCalculator.shouldNonRedirectCancel(result)) {
                event.setCancelled(true);
            }

            if (nonRedirectTotal != 0) {
                event.setAmount(Math.max(0, rawValue));
            }

            AttributeCalculator.applyRedirectAttributes(this.manager, player, null, "regen", Map.of("amount", event.getAmount()));
        }
    }
}

package net.slqmy.better_projectiles_plugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

import net.slqmy.better_projectiles_plugin.BetterProjectilesPlugin;

public class ProjectileHitListener implements Listener {

    private final BetterProjectilesPlugin plugin;

    public ProjectileHitListener(BetterProjectilesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileHit(@NotNull ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        Entity hitEntity = event.getHitEntity();

        String projectileType = projectile.getType().toString().toLowerCase();

        Bukkit.getLogger().info("projectileType = " + projectileType);

        if (hitEntity == null) {
            return;
        } else if (hitEntity instanceof Snowman snowGolem) {
            if (projectile instanceof Snowball) {
                event.setCancelled(true);

                double health = snowGolem.getHealth();
                AttributeInstance maxHealthAttribute = snowGolem.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                assert maxHealthAttribute != null;

                double maxHealth = maxHealthAttribute.getValue();

                snowGolem.setHealth(Math.min(health + maxHealth / 16.0D, maxHealth));
            }
        }

        Bukkit.getLogger().info("hitEntity.type = " + hitEntity.getType());

        YamlConfiguration configuration = (YamlConfiguration) plugin.getConfig();
        ConfigurationSection configurationSection = configuration.getConfigurationSection("projectiles." + projectileType);

        if (configurationSection == null) {
            return;
        }

        double damage = configurationSection.getDouble("damage");
        double knockback = configurationSection.getDouble("knockback");

        ProjectileSource source = projectile.getShooter();

        hitEntity.setVelocity(projectile.getVelocity().normalize().multiply(knockback/20.0D));
    }
}

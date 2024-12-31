package dev.enderman.minecraft.plugins.projectiles.better.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

import dev.enderman.minecraft.plugins.projectiles.better.BetterProjectilesPlugin;

public class FireballSpawnListener implements Listener {

    private final BetterProjectilesPlugin plugin;

    public FireballSpawnListener(BetterProjectilesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFireBallSpawn(@NotNull EntitySpawnEvent event) {
        if (event.getEntity() instanceof Fireball fireball) {
            ProjectileSource source = fireball.getShooter();

            if (source instanceof Ghast ghast) {
                PersistentDataContainer container = ghast.getPersistentDataContainer();

                NamespacedKey nuclearGhastMobKey = plugin.getNuclearGhastMobKey();

                boolean isNuclearGhast = Boolean.TRUE.equals(container.get(nuclearGhastMobKey, PersistentDataType.BOOLEAN));

                if (isNuclearGhast) {

                }
            }
        }
    }
}

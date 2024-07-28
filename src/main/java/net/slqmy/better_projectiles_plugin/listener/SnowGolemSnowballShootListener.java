package net.slqmy.better_projectiles_plugin.listener;

import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import net.slqmy.better_projectiles_plugin.BetterProjectilesPlugin;

public class SnowGolemSnowballShootListener implements Listener {

    private final BetterProjectilesPlugin plugin;

    public SnowGolemSnowballShootListener(BetterProjectilesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSnowGolemSnowballShoot(@NotNull ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();

        if (projectile.getShooter() instanceof Snowman) {
            PersistentDataContainer dataContainer = projectile.getPersistentDataContainer();

            dataContainer.set(plugin.getSnowGolemSnowballKey(), PersistentDataType.BOOLEAN, true);
        }
    }
}

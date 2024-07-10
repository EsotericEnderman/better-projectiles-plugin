package net.slqmy.better_projectiles.listener;

import org.bukkit.Location;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.NotNull;

public class SnowGolemShotListener implements Listener {

    @EventHandler
    public void onSnowGolemShot(@NotNull ProjectileHitEvent event) {
        if (event.getHitEntity() instanceof Snowman snowGolem) {
            Location eyeLocation = snowGolem.getEyeLocation();
            Location hitLocation = event.getEntity().getLocation();

            if (Math.abs(eyeLocation.getY() - hitLocation.getY()) > 0.3D) {
                event.setCancelled(true);
            }
        }
    }
}

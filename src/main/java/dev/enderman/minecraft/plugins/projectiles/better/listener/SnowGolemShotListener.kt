package dev.enderman.minecraft.plugins.projectiles.better.listener

import org.bukkit.entity.Snowman
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import kotlin.math.abs

class SnowGolemShotListener : Listener {

  @EventHandler
  fun onSnowGolemShot(event: ProjectileHitEvent) {
    val entity = event.hitEntity

    if (entity is Snowman) {
      val eyeLocation = entity.eyeLocation
      val hitLocation = event.entity.location

      if (abs(eyeLocation.y - hitLocation.y) > 0.3) event.isCancelled = true
    }
  }
}

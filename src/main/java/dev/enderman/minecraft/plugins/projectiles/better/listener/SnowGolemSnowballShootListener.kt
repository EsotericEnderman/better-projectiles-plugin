package dev.enderman.minecraft.plugins.projectiles.better.listener

import dev.enderman.minecraft.plugins.projectiles.better.BetterProjectilesPlugin
import org.bukkit.entity.Snowman
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.persistence.PersistentDataType

class SnowGolemSnowballShootListener(private val plugin: BetterProjectilesPlugin) : Listener {

  @EventHandler
  fun onSnowGolemSnowballShoot(event: ProjectileLaunchEvent) {
    val projectile = event.entity

    if (projectile.shooter is Snowman) {
      val dataContainer = projectile.persistentDataContainer

      dataContainer.set(plugin.snowGolemSnowballKey, PersistentDataType.BOOLEAN, true)
    }
  }
}

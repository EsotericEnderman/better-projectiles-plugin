package dev.enderman.minecraft.plugins.projectiles.better.listener

import dev.enderman.minecraft.plugins.projectiles.better.BetterProjectilesPlugin
import org.bukkit.entity.Fireball
import org.bukkit.entity.Ghast
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.persistence.PersistentDataType

class FireballSpawnListener(private val plugin: BetterProjectilesPlugin) : Listener {

  @EventHandler
  fun onFireBallSpawn(event: ProjectileLaunchEvent) {
    val entity = event.entity

    if (entity is Fireball) {
      val source = entity.shooter

      if (source is Ghast) {
        val container = source.persistentDataContainer
        val nuclearGhastMobKey = plugin.nuclearGhastMobKey
        val isNuclearGhast = true == container.get(nuclearGhastMobKey, PersistentDataType.BOOLEAN)

        if (isNuclearGhast) {
          val projectileContainer = entity.persistentDataContainer
          projectileContainer.set(plugin.nuclearFireballKey, PersistentDataType.BOOLEAN, true)
        }
      }
    }
  }
}

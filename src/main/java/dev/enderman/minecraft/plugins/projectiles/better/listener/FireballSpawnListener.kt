package dev.enderman.minecraft.plugins.projectiles.better.listener

import dev.enderman.minecraft.plugins.projectiles.better.BetterProjectilesPlugin
import org.bukkit.entity.Fireball
import org.bukkit.entity.Ghast
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.persistence.PersistentDataType

class FireballSpawnListener(private val plugin: BetterProjectilesPlugin) : Listener {

  @EventHandler
  fun onFireBallSpawn(event: EntitySpawnEvent) {
    val entity = event.entity

    if (entity is Fireball) {
      val source = entity.shooter

      if (source is Ghast) {
        val container = source.persistentDataContainer

        val nuclearGhastMobKey = plugin.nuclearGhastMobKey

        val isNuclearGhast = java.lang.Boolean.TRUE == container.get(
          nuclearGhastMobKey, PersistentDataType.BOOLEAN
        )

        if (isNuclearGhast) {
        }
      }
    }
  }
}

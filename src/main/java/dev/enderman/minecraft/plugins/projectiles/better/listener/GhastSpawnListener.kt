package dev.enderman.minecraft.plugins.projectiles.better.listener

import dev.enderman.minecraft.plugins.projectiles.better.BetterProjectilesPlugin
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Ghast
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.*

class GhastSpawnListener(private val plugin: BetterProjectilesPlugin) : Listener {

  @EventHandler
  fun onGhastSpawn(event: EntitySpawnEvent) {
    val entity = event.entity

    if (entity is Ghast) {
      val configuration = plugin.config as YamlConfiguration

      val nuclearGhastsEnabled = configuration.getBoolean("nuclear-ghasts.enabled")

      if (!nuclearGhastsEnabled) return

      val nuclearGhastSpawnChance = configuration.getDouble("nuclear-ghasts.spawn-chance")

      val randomNumber = random.nextDouble()

      if (randomNumber > nuclearGhastSpawnChance) return

      val dataContainer: PersistentDataContainer = entity.getPersistentDataContainer()

      val nuclearGhastMobKey = plugin.nuclearGhastMobKey

      dataContainer.set(nuclearGhastMobKey, PersistentDataType.BOOLEAN, true)
    }
  }

  companion object {
    private val random = Random()
  }
}

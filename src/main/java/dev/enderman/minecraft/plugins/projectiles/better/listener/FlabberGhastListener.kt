package dev.enderman.minecraft.plugins.projectiles.better.listener

import dev.enderman.minecraft.plugins.projectiles.better.BetterProjectilesPlugin
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Ghast
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.persistence.PersistentDataType
import java.util.Random

class FlabberGhastListener(private val plugin : BetterProjectilesPlugin) : Listener {

  @EventHandler
  private fun onGhastSpawn(event: EntitySpawnEvent) {
    val entity = event.entity

    if (entity !is Ghast) return

    val configuration = plugin.config as YamlConfiguration

    val flabberGhastsEnabled = configuration.getBoolean("flabberghasts.enabled")

    if (!flabberGhastsEnabled) return

    val flabberGhastSpawnChance = configuration.getDouble("flabberghasts.spawn-chance")

    val randomNumber = random.nextDouble()

    if (randomNumber > flabberGhastSpawnChance) return

    val dataContainer = entity.persistentDataContainer

    dataContainer.set(plugin.flabberGhastMobKey, PersistentDataType.BOOLEAN, true)
  }

  @EventHandler
  private fun onAggro(event: EntityTargetLivingEntityEvent) {
    val entity = event.entity

    if (entity !is Ghast) return

    val dataContainer = entity.persistentDataContainer
    val isFlabberGhast = dataContainer.get(plugin.flabberGhastMobKey, PersistentDataType.BOOLEAN) == true

    if (!isFlabberGhast) return

    event.isCancelled = true
  }

  companion object {
    private val random = Random()
  }
}

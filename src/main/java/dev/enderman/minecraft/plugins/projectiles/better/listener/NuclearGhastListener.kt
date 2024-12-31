package dev.enderman.minecraft.plugins.projectiles.better.listener

import dev.enderman.minecraft.plugins.projectiles.better.BetterProjectilesPlugin
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.EntityType
import org.bukkit.entity.Fireball
import org.bukkit.entity.Ghast
import org.bukkit.entity.ThrownPotion
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

class NuclearGhastListener(private val plugin: BetterProjectilesPlugin) : Listener {

  @EventHandler
  fun onNuclearGhastDeath(event: EntityDeathEvent) {
    val entity = event.entity

    if (entity is Ghast) {
      val container = entity.persistentDataContainer

      val isNuclearGhast = true == container.get(plugin.nuclearGhastMobKey, PersistentDataType.BOOLEAN)

      if (!isNuclearGhast) return

      val configuration = plugin.config as YamlConfiguration

      val nuclearGhastDeathSettings = configuration.getConfigurationSection("nuclear-ghasts.death")!!

      val poisonSettings = nuclearGhastDeathSettings.getConfigurationSection("poison")!!
      val explosionSettings = nuclearGhastDeathSettings.getConfigurationSection("explosion")!!

      val explosionEnabled = explosionSettings.getBoolean("enabled")

      val world = entity.world
      val location = entity.location

      if (explosionEnabled) {
        plugin.logger.info("NUCLEAR GHAST EXPLOSION IMMINENT!")

        world.createExplosion(
          location,
          explosionSettings.getDouble("power").toFloat(),
          explosionSettings.getBoolean("set-fire"),
          explosionSettings.getBoolean("break-blocks"),
          entity
        )
      }

      val poisonEnabled = poisonSettings.getBoolean("enabled")

      if (poisonEnabled) {
        val potion = world.spawnEntity(location, EntityType.POTION) as ThrownPotion

        val item = potion.item
        val meta = item.itemMeta as PotionMeta

        meta.addCustomEffect(
          PotionEffect(
            PotionEffectType.POISON,
            poisonSettings.getInt("duration-seconds") * 20,
            poisonSettings.getInt("potency") - 1,
            true,
            true,
            true
          ),
          true
        )

        item.itemMeta = meta
        potion.item = item

        potion.splash()
      }
    }
  }

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
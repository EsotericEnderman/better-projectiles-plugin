package dev.enderman.minecraft.plugins.projectiles.better.listener

import dev.enderman.minecraft.plugins.projectiles.better.BetterProjectilesPlugin
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.EntityType
import org.bukkit.entity.Ghast
import org.bukkit.entity.ThrownPotion
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class NuclearGhastDeathListener(private val plugin: BetterProjectilesPlugin) : Listener {

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
      }
    }
  }
}

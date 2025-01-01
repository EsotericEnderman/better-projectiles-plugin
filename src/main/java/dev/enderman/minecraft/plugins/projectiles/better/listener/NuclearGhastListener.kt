package dev.enderman.minecraft.plugins.projectiles.better.listener

import dev.enderman.minecraft.plugins.projectiles.better.BetterProjectilesPlugin
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.EntityType
import org.bukkit.entity.Fireball
import org.bukkit.entity.Ghast
import org.bukkit.entity.ThrownPotion
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
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

        val newItem = ItemStack(if (poisonSettings.getBoolean("lingering")) Material.LINGERING_POTION else Material.POTION)
        val meta = newItem.itemMeta as PotionMeta

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

        newItem.itemMeta = meta
        potion.item = newItem

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

          entity.yield = 0F
        }
      }
    }
  }

  @EventHandler
  private fun onNuclearFireballHit(event: ProjectileHitEvent) {
    val entity = event.entity

    if (entity !is Fireball) return

    val shooter = entity.shooter
    if (shooter !is Ghast) return

    if (entity.yield != 0F) return

    val container = entity.persistentDataContainer
    val isNuclear = container.get(plugin.nuclearFireballKey, PersistentDataType.BOOLEAN) == true

    if (!isNuclear) return

    val configuration = plugin.config.getConfigurationSection("nuclear-ghasts.fireballs.explosion")!!

    val power = configuration.getDouble("power").toFloat()
    val setFire = configuration.getBoolean("set-fire")
    val breakBlocks = configuration.getBoolean("break-blocks")

    entity.world.createExplosion(entity.location, power, setFire, breakBlocks, shooter)
  }

  @EventHandler(priority = EventPriority.HIGH)
  fun onGhastSpawn(event: EntitySpawnEvent) {
    val entity = event.entity

    if (entity is Ghast) {
      val configuration = plugin.config as YamlConfiguration

      val nuclearGhastsEnabled = configuration.getBoolean("nuclear-ghasts.enabled")

      if (!nuclearGhastsEnabled) return

      val nuclearGhastSpawnChance = configuration.getDouble("nuclear-ghasts.spawn-chance")

      val randomNumber = random.nextDouble()

      if (randomNumber > nuclearGhastSpawnChance) return

      val dataContainer = entity.getPersistentDataContainer()

      val nuclearGhastMobKey = plugin.nuclearGhastMobKey

      dataContainer.set(nuclearGhastMobKey, PersistentDataType.BOOLEAN, true)

      val customName = entity.customName() ?: Component.translatable("entity.minecraft.ghast")

      val nuclear = Component.text("☢ ")
      val newCustomName = nuclear.append(customName)

      entity.customName(newCustomName)
    }
  }

  companion object {
    private val random = Random()
  }
}

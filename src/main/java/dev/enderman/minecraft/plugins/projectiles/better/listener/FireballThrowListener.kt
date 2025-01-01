package dev.enderman.minecraft.plugins.projectiles.better.listener

import dev.enderman.minecraft.plugins.projectiles.better.BetterProjectilesPlugin
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Fireball
import org.bukkit.entity.Projectile
import org.bukkit.entity.SmallFireball
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType
import kotlin.math.max

class FireballThrowListener(private val plugin: BetterProjectilesPlugin) : Listener {

  @EventHandler
  fun onFireballThrow(event: PlayerInteractEvent) {
    val player = event.player
    val inventory = player.inventory

    val heldItem = inventory.itemInMainHand

    if (heldItem.type == Material.FIRE_CHARGE && event.clickedBlock == null) {
      val configuration = plugin.config as YamlConfiguration

      var fireChargeSetting = configuration.getConfigurationSection("projectiles.fire-charge")!!

      var fireballClass: Class<out Projectile> = SmallFireball::class.java

      val dataContainer = heldItem.itemMeta.persistentDataContainer

      val isGhastFireball = true == dataContainer.get(plugin.ghastFireballItemKey, PersistentDataType.BOOLEAN)

      if (isGhastFireball) {
        fireballClass = Fireball::class.java
        fireChargeSetting = configuration.getConfigurationSection("projectiles.ghast-fireball")!!
      }

      val fireChargeThrowingEnabled = fireChargeSetting.getBoolean("throwing.enabled")

      if (!fireChargeThrowingEnabled) return

      val playerLocation = player.location
      val playerWorld = playerLocation.world

      val fireball = playerWorld.spawn(player.eyeLocation, fireballClass)

      val fireChargeSpeed = fireChargeSetting.getDouble("speed")

      fireball.velocity = playerLocation.direction.multiply(fireChargeSpeed / 20.0)

      fireball.shooter = player

      val hungerConsumption = fireChargeSetting.getInt("throwing.hunger-consumption")

      player.foodLevel = max((player.foodLevel - hungerConsumption).toDouble(), 0.0).toInt()

      if (player.gameMode == GameMode.CREATIVE) return

      heldItem.amount -= 1
    }
  }
}

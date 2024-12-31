package dev.enderman.minecraft.plugins.projectiles.better.listener

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Snowman
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.EquipmentSlot
import kotlin.math.min

class SnowGolemRightClickListener : Listener {

  @EventHandler
  fun onSnowGolemRightClick(event: PlayerInteractAtEntityEvent) {
    val entity = event.rightClicked

    if (entity !is Snowman) return
    if (event.hand != EquipmentSlot.HAND) return

    val health = entity.health

    val maxHealthAttributeInstance = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!

    val maxHealth = maxHealthAttributeInstance.value

    if (health == maxHealth) return

    val player = event.player
    val inventory = player.inventory

    val healthIncrease: Double

    // Snow golem has maxHealth health.
    // Snow golem is made of 2 snow blocks.
    // Snow golem is made of 8 snowballs.
    var heldItem = inventory.itemInMainHand

    when (heldItem.type) {
      Material.SNOW_BLOCK -> healthIncrease = maxHealth / 2.0
      Material.SNOWBALL -> healthIncrease = maxHealth / 8.0
      else -> {
        heldItem = inventory.itemInOffHand

        healthIncrease = when (heldItem.type) {
          Material.SNOW_BLOCK -> maxHealth / 2.0
          Material.SNOWBALL -> maxHealth / 8.0
          else -> return
        }
      }
    }

    entity.health = min(health + healthIncrease, maxHealth)
    if (player.gameMode != GameMode.CREATIVE) heldItem.amount -= 1
  }
}

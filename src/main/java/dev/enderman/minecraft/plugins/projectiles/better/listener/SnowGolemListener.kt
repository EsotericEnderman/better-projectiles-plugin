package dev.enderman.minecraft.plugins.projectiles.better.listener

import dev.enderman.minecraft.plugins.projectiles.better.BetterProjectilesPlugin
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.entity.Snowman
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.math.min

class SnowGolemListener(private val plugin: BetterProjectilesPlugin) : Listener {

  @EventHandler
  fun onSnowGolemSpawn(event: EntitySpawnEvent) {
    val entity = event.entity

    if (entity is Snowman) {
      val maxHealthAttribute = checkNotNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH))
      val configuration = plugin.config as YamlConfiguration
      val snowGolemHealth = configuration.getDouble("snow-golems.health")

      maxHealthAttribute.addModifier(
        AttributeModifier(plugin.snowGolemHealthIncreaseAttributeModifierKey, snowGolemHealth - entity.health, AttributeModifier.Operation.ADD_NUMBER)
      )

      entity.health = snowGolemHealth
    }
  }

  @EventHandler
  fun onSnowGolemSnowballShoot(event: ProjectileLaunchEvent) {
    val projectile = event.entity

    if (projectile.shooter is Snowman) {
      val dataContainer = projectile.persistentDataContainer

      dataContainer.set(plugin.snowGolemSnowballKey, PersistentDataType.BOOLEAN, true)
    }
  }

  @EventHandler
  fun onSnowGolemRightClick(event: PlayerInteractAtEntityEvent) {
    val entity = event.rightClicked

    if (entity !is Snowman) return
    if (event.hand != EquipmentSlot.HAND) return

    val health = entity.health

    if (health <= 0) return

    val maxHealthAttributeInstance = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!

    val maxHealth = maxHealthAttributeInstance.value

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
          else -> {
            onSnowGolemSnowTake(player, entity)
            return
          }
        }
      }
    }

    val isLargeHealthIncrease = healthIncrease == maxHealth / 2.0

    val finalHealth = min(health + healthIncrease, maxHealth)
    val actualAmountHealed = finalHealth - health

    if (actualAmountHealed != 0.0) {
      entity.health = finalHealth
      entity.world.spawnParticle(Particle.HEART, entity.location, actualAmountHealed.toInt(), 0.5, 0.25, 0.5)

      if (player.gameMode != GameMode.CREATIVE) {
        heldItem.amount -= 1

        if (isLargeHealthIncrease && actualAmountHealed != maxHealth / 2.0) {
          val snowBallCompensation = (maxHealth / 2.0 - actualAmountHealed).toInt()
          player.inventory.addItem(ItemStack(Material.SNOWBALL, snowBallCompensation))
        }
      }
    }
  }

  private fun onSnowGolemSnowTake(player: Player, golem: Snowman) {
    if (golem.noDamageTicks > 10) return

    val health = golem.health

    val maxHealthAttributeInstance = golem.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!
    val maxHealth = maxHealthAttributeInstance.value

    val isLargeHealthDecrease = player.isSneaking && health >= maxHealth / 2.0

    val healthDecrease = if (isLargeHealthDecrease) maxHealth / 2.0 else maxHealth / 8.0

    golem.damage(healthDecrease, player)
    player.inventory.addItem(ItemStack(if (isLargeHealthDecrease) Material.SNOW_BLOCK else Material.SNOWBALL))
  }

  @EventHandler
  private fun onSnowGolemDeath(event: EntityDeathEvent) {
    val entity = event.entity
    if (entity !is Snowman) return
    event.drops.clear()
  }

  @EventHandler
  private fun onSnowGolemDamage(event: EntityDamageEvent) {
    val entity = event.entity
    if (entity !is Snowman) return

    val health = entity.health
    val previousHealth = health + event.finalDamage

    val previousSnowballs = previousHealth.toInt()
    val currentSnowballs = health.toInt()

    val maxHealthAttribute = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!
    val maxHealth = maxHealthAttribute.value

    if (currentSnowballs != previousSnowballs) {
      val lostSnowballCount = min((previousSnowballs - currentSnowballs).toDouble(), maxHealth).toInt()

      val snowballsToDrop = lostSnowballCount % 4
      val snowBlocksToDrop = (lostSnowballCount - snowballsToDrop) / 4

      val world = entity.world

      if (snowBlocksToDrop != 0) {
        world.dropItemNaturally(entity.location, ItemStack(Material.SNOW_BLOCK, snowBlocksToDrop))
      }

      if (snowballsToDrop != 0) {
        world.dropItemNaturally(entity.location, ItemStack(Material.SNOWBALL, snowballsToDrop))
      }
    }
  }
}

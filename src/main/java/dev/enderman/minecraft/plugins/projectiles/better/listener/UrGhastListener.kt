package dev.enderman.minecraft.plugins.projectiles.better.listener

import dev.enderman.minecraft.plugins.projectiles.better.BetterProjectilesPlugin
import net.kyori.adventure.text.Component
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Fireball
import org.bukkit.entity.Ghast
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.persistence.PersistentDataType
import java.util.*

class UrGhastListener(private val plugin : BetterProjectilesPlugin) : Listener {

  @EventHandler(priority = EventPriority.LOW)
  private fun onGhastSpawn(event: EntitySpawnEvent) {
    val entity = event.entity

    if (entity is Ghast) {
      val configuration = plugin.config as YamlConfiguration

      val urGhastsEnabled = configuration.getBoolean("ur-ghasts.enabled")

      if (!urGhastsEnabled) return

      val urGhastSpawnChance = configuration.getDouble("ur-ghasts.spawn-chance")

      val randomNumber = random.nextDouble()

      if (randomNumber > urGhastSpawnChance) return

      val extraScale = configuration.getDouble("ur-ghasts.extra-scale")

      val scaleAttribute = entity.getAttribute(Attribute.SCALE)!!
      val scaleModifier = AttributeModifier(plugin.urGhastScaleKey, extraScale, AttributeModifier.Operation.ADD_NUMBER)
      scaleAttribute.addModifier(scaleModifier)

      val extraHealth = configuration.getDouble("ur-ghasts.extra-health")

      val maxHealthAttribute = entity.getAttribute(Attribute.MAX_HEALTH)!!
      val maxHealthModifier = AttributeModifier(plugin.urGhastHealthKey, extraHealth, AttributeModifier.Operation.ADD_NUMBER)
      maxHealthAttribute.addModifier(maxHealthModifier)

      entity.health += extraHealth

      val extraDetectionRange = configuration.getDouble("ur-ghasts.extra-detection-range")

      val detectionRangeAttribute = entity.getAttribute(Attribute.FOLLOW_RANGE)!!
      val detectionRangeModifier = AttributeModifier(plugin.urGhastDetectionRangeKey, extraDetectionRange, AttributeModifier.Operation.ADD_NUMBER)
      detectionRangeAttribute.addModifier(detectionRangeModifier)

      val ghastNameComponent = Component.translatable("entity.minecraft.ghast")
      val urGhastNameComponent = Component.text("Ur-").append(ghastNameComponent)

      entity.customName(urGhastNameComponent)
      entity.isCustomNameVisible = false
    }
  }

  @EventHandler
  fun onFireBallSpawn(event: ProjectileLaunchEvent) {
    val entity = event.entity

    if (entity is Fireball) {
      val source = entity.shooter

      if (source is Ghast) {
        val scaleAttribute = source.getAttribute(Attribute.SCALE)!!
        val value = scaleAttribute.value

        val isNuclearGhast = value != 1.0

        if (isNuclearGhast) {
          val projectileContainer = entity.persistentDataContainer
          projectileContainer.set(plugin.urGhastFireballKey, PersistentDataType.BOOLEAN, true)

          entity.yield = 0F
        }
      }
    }
  }

  @EventHandler
  private fun onUrFireballHit(event: ProjectileHitEvent) {
    val entity = event.entity

    if (entity !is Fireball) return

    val shooter = entity.shooter
    if (shooter !is Ghast) return

    if (entity.yield != 0F) return

    val container = entity.persistentDataContainer
    val isUrFireball = container.get(plugin.urGhastFireballKey, PersistentDataType.BOOLEAN) == true

    if (!isUrFireball) return

    val configuration = plugin.config.getConfigurationSection("ur-ghasts.fireballs.explosion")!!

    val power = configuration.getDouble("power").toFloat()
    val setFire = configuration.getBoolean("set-fire")
    val breakBlocks = configuration.getBoolean("break-blocks")

    entity.world.createExplosion(entity.location, power, setFire, breakBlocks, shooter)
  }

  companion object {
    private val random = Random()
  }
}

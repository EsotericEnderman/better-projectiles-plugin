package dev.enderman.minecraft.plugins.projectiles.better.listener

import dev.enderman.minecraft.plugins.projectiles.better.BetterProjectilesPlugin
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Ghast
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent
import java.util.Random

class BlackHoleGhastListener(private val plugin: BetterProjectilesPlugin) : Listener {

  @EventHandler(priority = EventPriority.HIGH)
  fun onGhastSpawn(event: EntitySpawnEvent) {
    val entity = event.entity

    if (entity !is Ghast) return

    val configuration = plugin.config as YamlConfiguration

    val blackHoleGhastsEnabled = configuration.getBoolean("black-hole-ghasts.enabled")

    if (!blackHoleGhastsEnabled) return

    val blackHoleGhastSpawnChance = configuration.getDouble("black-hole-ghasts.spawn-chance")

    val randomNumber = random.nextDouble()

    if (randomNumber > blackHoleGhastSpawnChance) return

    val reducedScale = -configuration.getDouble("black-hole-ghasts.reduced-scale")

    val scaleAttribute = entity.getAttribute(Attribute.SCALE)!!
    val scaleModifier = AttributeModifier(plugin.blackHoleGhastScaleKey, reducedScale, AttributeModifier.Operation.ADD_NUMBER)
    scaleAttribute.addModifier(scaleModifier)

    val extraHealth = configuration.getDouble("black-hole-ghasts.extra-health")

    val maxHealthAttribute = entity.getAttribute(Attribute.MAX_HEALTH)!!
    val maxHealthModifier = AttributeModifier(plugin.blackHoleGhastHealthKey, extraHealth, AttributeModifier.Operation.ADD_NUMBER)
    maxHealthAttribute.addModifier(maxHealthModifier)
  }

  companion object {
    private val random = Random()
  }
}

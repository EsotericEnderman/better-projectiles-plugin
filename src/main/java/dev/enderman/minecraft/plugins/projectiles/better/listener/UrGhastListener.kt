package dev.enderman.minecraft.plugins.projectiles.better.listener

import dev.enderman.minecraft.plugins.projectiles.better.BetterProjectilesPlugin
import net.kyori.adventure.text.Component
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Ghast
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent
import java.util.*

class UrGhastListener(private val plugin : BetterProjectilesPlugin) : Listener {

  @EventHandler
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

      val ghastNameComponent = Component.translatable("entity.minecraft.ghast")
      val urGhastNameComponent = Component.text("Ur-").append(ghastNameComponent)

      entity.customName(urGhastNameComponent)
      entity.isCustomNameVisible = false
    }
  }

  companion object {
    private val random = Random()
  }
}

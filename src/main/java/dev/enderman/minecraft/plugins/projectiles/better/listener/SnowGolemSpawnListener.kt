package dev.enderman.minecraft.plugins.projectiles.better.listener

import dev.enderman.minecraft.plugins.projectiles.better.BetterProjectilesPlugin
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Snowman
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent

class SnowGolemSpawnListener(private val plugin: BetterProjectilesPlugin) : Listener {

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
}

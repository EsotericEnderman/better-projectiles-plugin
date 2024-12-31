package dev.enderman.minecraft.plugins.projectiles.better.listener

import dev.enderman.minecraft.plugins.projectiles.better.BetterProjectilesPlugin
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Snowball
import org.bukkit.entity.Snowman
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import java.util.*
import kotlin.math.min

class ProjectileHitListener(private val plugin: BetterProjectilesPlugin) : Listener {

  @EventHandler
  fun onProjectileHit(event: ProjectileHitEvent) {
    val projectile = event.entity
    val hitEntity = event.hitEntity

    val projectileType = projectile.type.toString().lowercase(Locale.getDefault())

    Bukkit.getLogger().info("projectileType = $projectileType")

    if (hitEntity == null) return

    if (hitEntity is Snowman) {
      if (projectile is Snowball) {
        event.isCancelled = true

        val health = hitEntity.health
        val maxHealthAttribute = hitEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!
        val maxHealth = maxHealthAttribute.value

        hitEntity.health = min(health + maxHealth / 16.0, maxHealth)
      }
    }

    Bukkit.getLogger().info("hitEntity.type = " + hitEntity.type)

    val configuration = plugin.config as YamlConfiguration
    val configurationSection = configuration.getConfigurationSection(
      "projectiles.$projectileType"
    )

    if (configurationSection == null) return

    val damage = configurationSection.getDouble("damage")
    val knockback = configurationSection.getDouble("knockback")

    val source = projectile.shooter

    hitEntity.velocity = projectile.velocity.normalize().multiply(knockback / 20.0)
  }
}

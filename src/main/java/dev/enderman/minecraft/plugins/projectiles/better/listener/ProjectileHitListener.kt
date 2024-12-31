package dev.enderman.minecraft.plugins.projectiles.better.listener

import dev.enderman.minecraft.plugins.projectiles.better.BetterProjectilesPlugin
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import java.util.*

class ProjectileHitListener(private val plugin: BetterProjectilesPlugin) : Listener {

  @EventHandler
  fun onProjectileHit(event: ProjectileHitEvent) {
    val projectile = event.entity
    val hitEntity = event.hitEntity

    val projectileType = projectile.type.toString().lowercase(Locale.getDefault())

    plugin.logger.info("projectileType = $projectileType")

    if (hitEntity == null) return

    plugin.logger.info("hitEntity.type = " + hitEntity.type)

    val configuration = plugin.config as YamlConfiguration
    val configurationSection = configuration.getConfigurationSection("projectiles.$projectileType") ?: return

    val damage = configurationSection.getDouble("damage")
    val knockback = configurationSection.getDouble("knockback")

    val source = projectile.shooter

    hitEntity.velocity = projectile.velocity.normalize().multiply(knockback / 20.0)
  }
}

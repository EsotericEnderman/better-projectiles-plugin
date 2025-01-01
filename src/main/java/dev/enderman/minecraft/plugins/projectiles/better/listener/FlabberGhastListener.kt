package dev.enderman.minecraft.plugins.projectiles.better.listener

import dev.enderman.minecraft.plugins.projectiles.better.BetterProjectilesPlugin
import net.minecraft.world.entity.MoverType
import net.minecraft.world.phys.Vec3
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.craftbukkit.entity.CraftGhast
import org.bukkit.entity.Ghast
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitTask
import java.util.Random
import kotlin.math.min

class FlabberGhastListener(private val plugin : BetterProjectilesPlugin) : Listener {

  @EventHandler
  private fun onGhastSpawn(event: EntitySpawnEvent) {
    val entity = event.entity

    if (entity !is Ghast) return

    val configuration = plugin.config as YamlConfiguration

    val flabberGhastsEnabled = configuration.getBoolean("flabberghasts.enabled")

    if (!flabberGhastsEnabled) return

    val flabberGhastSpawnChance = configuration.getDouble("flabberghasts.spawn-chance")

    val randomNumber = random.nextDouble()

    if (randomNumber > flabberGhastSpawnChance) return

    val dataContainer = entity.persistentDataContainer

    dataContainer.set(plugin.flabberGhastMobKey, PersistentDataType.BOOLEAN, true)
  }

  @EventHandler
  private fun onAggro(event: EntityTargetLivingEntityEvent) {
    val entity = event.entity

    if (entity !is Ghast) return

    val dataContainer = entity.persistentDataContainer
    val isFlabberGhast = dataContainer.get(plugin.flabberGhastMobKey, PersistentDataType.BOOLEAN) == true

    if (!isFlabberGhast) return

    event.isCancelled = true
  }

  @EventHandler
  private fun onHurt(event: EntityDamageByEntityEvent) {
    val entity = event.entity

    if (entity !is Ghast) return

    val dataContainer = entity.persistentDataContainer
    val isFlabberGhasted = dataContainer.get(plugin.isFlabberGhastedKey, PersistentDataType.BOOLEAN) == true

    if (isFlabberGhasted) return

    dataContainer.set(plugin.isFlabberGhastedKey, PersistentDataType.BOOLEAN, true)

    var iterations = 0

    var task: BukkitTask? = null

    val runnable = Runnable {
      plugin.logger.info("Iterations: $iterations")

      if (iterations == 25 || entity.isDead) {
        task!!.cancel()

        dataContainer.set(plugin.isFlabberGhastedKey, PersistentDataType.BOOLEAN, false)

        return@Runnable
      }

      val location = entity.location
      val lookingDirection = location.direction

      val movement = lookingDirection.multiply(5.0)

      plugin.logger.info("Updating flabberghast position!")

      var newVelocity = entity.velocity.add(movement)
      val magnitude = newVelocity.length()
      val newMagnitude = min(magnitude, 1.25)
      newVelocity = newVelocity.normalize().multiply(newMagnitude)

      entity.velocity = newVelocity

      iterations++
    }

    plugin.logger.info("Starting new flabberghast task...")

    task = plugin.server.scheduler.runTaskTimer(plugin, runnable, 2L, 2L)
  }

  companion object {
    private val random = Random()
  }
}

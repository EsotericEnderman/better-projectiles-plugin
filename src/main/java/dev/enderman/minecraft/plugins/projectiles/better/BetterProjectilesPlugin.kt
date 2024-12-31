package dev.enderman.minecraft.plugins.projectiles.better

import dev.enderman.minecraft.plugins.projectiles.better.listener.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

class BetterProjectilesPlugin : JavaPlugin() {

  private val ghastFireballRecipeKey = NamespacedKey(this, "ghast_fireball_recipe")
  val snowGolemSnowballKey = NamespacedKey(this, "snow_golem_snowball")
  val snowGolemHealthIncreaseAttributeModifierKey = NamespacedKey(this, "snow_golem_health_increase_attribute_modifier")
  val ghastFireballItemKey = NamespacedKey(this, "ghast_fireball_item")
  val nuclearGhastMobKey = NamespacedKey(this, "nuclear_ghast")

  override fun onEnable() {
    val configuration = config as YamlConfiguration

    configuration.options().copyDefaults()
    saveDefaultConfig()

    val pluginManager = Bukkit.getPluginManager()

    pluginManager.registerEvents(SnowGolemRightClickListener(), this)
    pluginManager.registerEvents(SnowGolemShotListener(), this)

    pluginManager.registerEvents(FireballThrowListener(this), this)
    pluginManager.registerEvents(SnowGolemSpawnListener(this), this)
    pluginManager.registerEvents(ProjectileHitListener(this), this)
    pluginManager.registerEvents(SnowGolemSnowballShootListener(this), this)
    pluginManager.registerEvents(GhastSpawnListener(this), this)
    pluginManager.registerEvents(NuclearGhastDeathListener(this), this)

    val ghastFireballsCraftable = configuration.getBoolean("projectiles.ghast-fireballs.craftable")

    if (!ghastFireballsCraftable) return

    val ghastFireball = ItemStack(Material.FIRE_CHARGE)

    val fireballMeta = ghastFireball.itemMeta

    fireballMeta.displayName(Component.translatable("entity.minecraft.fireball").decoration(TextDecoration.ITALIC, false))

    val dataContainer = fireballMeta.persistentDataContainer

    dataContainer.set(ghastFireballItemKey, PersistentDataType.BOOLEAN, true)

    ghastFireball.setItemMeta(fireballMeta)

    val ghastFireballRecipe = ShapedRecipe(ghastFireballRecipeKey, ghastFireball)

    ghastFireballRecipe.shape("GGG", "GFG", "GGG")

    ghastFireballRecipe.setIngredient('G', Material.GUNPOWDER)
    ghastFireballRecipe.setIngredient('F', Material.FIRE_CHARGE)

    Bukkit.addRecipe(ghastFireballRecipe)
  }
}

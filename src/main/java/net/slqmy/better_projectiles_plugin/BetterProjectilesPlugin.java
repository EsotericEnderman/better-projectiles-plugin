package net.slqmy.better_projectiles_plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.slqmy.better_projectiles_plugin.listener.*;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterProjectilesPlugin extends JavaPlugin {

    private final NamespacedKey snowGolemSnowballKey = new NamespacedKey(this, "snow_golem_snowball");

    private final NamespacedKey ghastFireballRecipeKey = new NamespacedKey(this, "ghast_fireball_recipe");

    private final NamespacedKey ghastFireballItemKey = new NamespacedKey(this, "ghast_fireball_item");

    private final NamespacedKey nuclearGhastMobKey = new NamespacedKey(this, "nuclear_ghast");

    public NamespacedKey getSnowGolemSnowballKey() {
        return snowGolemSnowballKey;
    }

    public NamespacedKey getGhastFireballRecipeKey() {
        return ghastFireballRecipeKey;
    }

    public NamespacedKey getGhastFireballItemKey() {
        return ghastFireballItemKey;
    }

    public NamespacedKey getNuclearGhastMobKey() {
        return nuclearGhastMobKey;
    }

    @Override
    public void onEnable() {
        YamlConfiguration configuration = (YamlConfiguration) getConfig();

        configuration.options().copyDefaults();
        saveDefaultConfig();

        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new SnowGolemRightClickListener(), this);
        pluginManager.registerEvents(new SnowGolemShotListener(), this);

        pluginManager.registerEvents(new FireballThrowListener(this), this);
        pluginManager.registerEvents(new SnowGolemSpawnListener(this), this);
        pluginManager.registerEvents(new ProjectileHitListener(this), this);
        pluginManager.registerEvents(new SnowGolemSnowballShootListener(this), this);
        pluginManager.registerEvents(new GhastSpawnListener(this), this);
        pluginManager.registerEvents(new NuclearGhastDeathListener(this), this);

        boolean ghastFireballsCraftable = configuration.getBoolean("projectiles.ghast-fireballs.craftable");

        if (!ghastFireballsCraftable) {
            return;
        }

        ItemStack ghastFireball = new ItemStack(Material.FIRE_CHARGE);

        ItemMeta fireballMeta = ghastFireball.getItemMeta();

        fireballMeta.displayName(Component.translatable("entity.minecraft.fireball").decoration(TextDecoration.ITALIC, false));

        PersistentDataContainer dataContainer = fireballMeta.getPersistentDataContainer();

        dataContainer.set(ghastFireballItemKey, PersistentDataType.BOOLEAN, true);

        ghastFireball.setItemMeta(fireballMeta);

        ShapedRecipe ghastFireballRecipe = new ShapedRecipe(ghastFireballRecipeKey, ghastFireball);

        ghastFireballRecipe.shape(
                "GGG",
                "GFG",
                "GGG"
        );

        ghastFireballRecipe.setIngredient('G', Material.GUNPOWDER);
        ghastFireballRecipe.setIngredient('F', Material.FIRE_CHARGE);

        Bukkit.addRecipe(ghastFireballRecipe);
    }
}

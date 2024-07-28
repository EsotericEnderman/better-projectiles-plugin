package net.slqmy.better_projectiles_plugin.listener;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import net.slqmy.better_projectiles_plugin.BetterProjectilesPlugin;

public class NuclearGhastDeathListener implements Listener {

    private final BetterProjectilesPlugin plugin;

    public NuclearGhastDeathListener(BetterProjectilesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNuclearGhastDeath(@NotNull EntityDeathEvent event) {
        if (event.getEntity() instanceof Ghast ghast) {
            PersistentDataContainer container = ghast.getPersistentDataContainer();

            boolean isNuclearGhast = Boolean.TRUE.equals(container.get(plugin.getNuclearGhastMobKey(), PersistentDataType.BOOLEAN));

            if (!isNuclearGhast) {
                return;
            }

            YamlConfiguration configuration = (YamlConfiguration) plugin.getConfig();

            ConfigurationSection nuclearGhastDeathSettings = configuration.getConfigurationSection("nuclear-ghasts.death");
            assert nuclearGhastDeathSettings != null;

            ConfigurationSection explosionSettings = nuclearGhastDeathSettings.getConfigurationSection("poison");
            assert explosionSettings != null;

            boolean explosionEnabled = explosionSettings.getBoolean("explosion.enabled");

            World world = ghast.getWorld();
            Location location = ghast.getLocation();

            if (explosionEnabled) {
                world.createExplosion(location,
                        (float) explosionSettings.getDouble("power"),
                        explosionSettings.getBoolean("set-fire"),
                        explosionSettings.getBoolean("break-blocks"),
                        ghast
                );
            }

            ConfigurationSection poisonSettings = nuclearGhastDeathSettings.getConfigurationSection("poison");
            assert poisonSettings != null;

            boolean poisonEnabled = poisonSettings.getBoolean("enabled");

            if (poisonEnabled) {
                ThrownPotion potion = (ThrownPotion) world.spawnEntity(location, EntityType.POTION);

                potion.getEffects().add( // error
                        new PotionEffect(
                                PotionEffectType.POISON,
                                poisonSettings.getInt("duration-seconds") * 20,
                                poisonSettings.getInt("potency") - 1,
                                true,
                                true,
                                true
                        )
                );
            }
        }
    }
}

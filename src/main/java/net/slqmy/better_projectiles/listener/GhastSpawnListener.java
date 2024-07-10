package net.slqmy.better_projectiles.listener;

import net.slqmy.better_projectiles.BetterProjectilesPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Ghast;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class GhastSpawnListener implements Listener {

    private final static Random random = new Random();

    private final BetterProjectilesPlugin plugin;

    public GhastSpawnListener(BetterProjectilesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onGhastSpawn(@NotNull EntitySpawnEvent event) {
        if (event.getEntity() instanceof Ghast ghast) {
            YamlConfiguration configuration = (YamlConfiguration) plugin.getConfig();

            boolean nuclearGhastsEnabled = configuration.getBoolean("nuclear-ghasts.enabled");

            if (!nuclearGhastsEnabled) {
                return;
            }

            double nuclearGhastSpawnChance = configuration.getDouble("nuclear-ghasts.spawn-chance");

            double randomNumber = random.nextDouble();

            if (randomNumber > nuclearGhastSpawnChance) {
                return;
            }

            PersistentDataContainer dataContainer = ghast.getPersistentDataContainer();

            NamespacedKey nuclearGhastMobKey = plugin.getNuclearGhastMobKey();

            dataContainer.set(nuclearGhastMobKey, PersistentDataType.BOOLEAN, true);
        }
    }
}

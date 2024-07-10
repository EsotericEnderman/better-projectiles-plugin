package net.slqmy.better_projectiles.listener;

import net.slqmy.better_projectiles.BetterProjectilesPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class FireballThrowListener implements Listener {

    private final BetterProjectilesPlugin plugin;

    public FireballThrowListener(BetterProjectilesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFireballThrow(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();

        ItemStack heldItem = inventory.getItemInMainHand();

        if (heldItem.getType() == Material.FIRE_CHARGE && event.getClickedBlock() == null) {
            YamlConfiguration configuration = (YamlConfiguration) plugin.getConfig();

            ConfigurationSection fireChargeSetting = configuration.getConfigurationSection("projectiles.fire-charge");
            assert fireChargeSetting != null;

            Class<? extends Fireball> fireballClass = SmallFireball.class;

            PersistentDataContainer dataContainer = heldItem.getItemMeta().getPersistentDataContainer();

            boolean isGhastFireball = Boolean.TRUE.equals(dataContainer.get(plugin.getGhastFireballItemKey(), PersistentDataType.BOOLEAN));

            if (isGhastFireball) {
                fireballClass = Fireball.class;
                fireChargeSetting = configuration.getConfigurationSection("projectiles.ghast-fireball");
            }

            assert fireChargeSetting != null;

            boolean fireChargeThrowingEnabled = fireChargeSetting.getBoolean("throwing.enabled");

            if (!fireChargeThrowingEnabled) {
                return;
            }

            Location playerLocation = player.getLocation();
            World playerWorld = playerLocation.getWorld();

            Fireball fireball = playerWorld.spawn(player.getEyeLocation(), fireballClass);

            double fireChargeSpeed = fireChargeSetting.getDouble("speed");

            fireball.setVelocity(playerLocation.getDirection().multiply(fireChargeSpeed / 20.0D));

            fireball.setShooter(player);

            int hungerConsumption = fireChargeSetting.getInt("throwing.hunger-consumption");

            player.setFoodLevel(Math.max(player.getFoodLevel() - hungerConsumption, 0));
        }
    }
}

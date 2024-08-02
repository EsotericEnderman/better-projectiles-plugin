package net.slqmy.better_projectiles_plugin.listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class SnowGolemRightClickListener implements Listener {

    @EventHandler
    public void onSnowGolemRightClick(@NotNull PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Snowman snowGolem)) {
          return;
        }

        double health = snowGolem.getHealth();

        AttributeInstance maxHealthAttributeInstance = snowGolem.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert maxHealthAttributeInstance != null;

        double maxHealth = maxHealthAttributeInstance.getValue();

        if (health == maxHealth) {
          return;
        }

        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();

        double healthIncrease;

        // Snow golem has maxHealth health.
        // Snow golem is made of 2 snow blocks.
        // Snow golem is made of 8 snowballs.

        ItemStack heldItem = inventory.getItemInMainHand();
        if (heldItem.getType() == Material.SNOW_BLOCK) {
          healthIncrease = maxHealth / 2.0D;
        } else if (heldItem.getType() == Material.SNOWBALL) {
          healthIncrease = maxHealth / 8.0D;
        } else {
          heldItem = inventory.getItemInOffHand();
          if (heldItem.getType() == Material.SNOW_BLOCK) {
            healthIncrease = maxHealth / 2.0D;
          } else if (heldItem.getType() == Material.SNOWBALL) {
            healthIncrease = maxHealth / 8.0D;
          } else {
            return;
          }
        }

        snowGolem.setHealth(Math.min(health + healthIncrease, maxHealth));
        if (player.getGameMode() != GameMode.CREATIVE) {
          heldItem.setAmount(heldItem.getAmount() - 1);
        }
    }
}

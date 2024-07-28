package net.slqmy.better_projectiles_plugin.listener;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.NotNull;

public class SnowGolemRightClickListener implements Listener {

    @EventHandler
    public void onSnowGolemRightClick(@NotNull PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Snowman snowGolem) {
            double health = snowGolem.getHealth();

            AttributeInstance maxHealthAttributeInstance = snowGolem.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            assert maxHealthAttributeInstance != null;

            double maxHealth = maxHealthAttributeInstance.getValue();

            snowGolem.setHealth(Math.min(health + maxHealth / 2.0D, maxHealth));
        }
    }
}

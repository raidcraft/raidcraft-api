package de.raidcraft.api.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ActionFactory;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.requirement.RequirementFactory;
import de.raidcraft.api.action.trigger.TriggerManager;
import de.raidcraft.api.action.trigger.global.PlayerTrigger;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author mdoering
 */
public final class ActionAPI {

    public static void registerGlobalActions(ActionFactory factory) {

        factory.registerGlobalAction("player.give.item", new Action<Player>() {
            @Override
            public void accept(Player player) {

                try {
                    ItemStack item = RaidCraft.getItem(getConfig().getString("item"), getConfig().getInt("amount", 1));
                    player.getInventory().addItem(item);
                } catch (CustomItemException e) {
                    RaidCraft.LOGGER.warning("player.give.item (" + player.getName() + "): " + e.getMessage());
                }
            }
        });
        factory.registerGlobalAction("player.kill", (Player player) -> player.setHealth(0.0));
    }

    public static void registerGlobalRequirements(RequirementFactory factory) {

        factory.registerGlobalRequirement("player.is-sprinting", Player::isSprinting);
        factory.registerGlobalRequirement("player.location", new Requirement<Player>() {
            @Override
            public boolean test(Player player) {

                Location location = player.getLocation();
                ConfigurationSection config = getConfig();
                World world = Bukkit.getWorld(config.getString("world", player.getWorld().getName()));
                if (config.isSet("world") && world == null) return false;

                if (config.isSet("x") && config.isSet("y") && config.isSet("z") && config.isSet("world")) {
                    if (config.isSet("radius")) {
                        return LocationUtil.isWithinRadius(location,
                                new Location(world,
                                        config.getInt("x"),
                                        config.getInt("y"),
                                        config.getInt("z")),
                                config.getInt("radius")
                        );
                    }
                    return config.getInt("x") == location.getBlockX()
                            && config.getInt("y") == location.getBlockY()
                            && config.getInt("z") == location.getBlockZ()
                            && world.equals(location.getWorld());
                }
                return false;
            }
        });
    }

    public static void registerGlobalTrigger(TriggerManager manager) {

        manager.registerGlobalTrigger(new PlayerTrigger());
    }
}

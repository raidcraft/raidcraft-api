package de.raidcraft.api.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ActionFactory;
import de.raidcraft.api.action.action.global.DoorAction;
import de.raidcraft.api.action.action.global.SetBlockAction;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.requirement.RequirementFactory;
import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.api.action.trigger.TriggerManager;
import de.raidcraft.api.action.trigger.global.PlayerTrigger;
import de.raidcraft.api.economy.AccountType;
import de.raidcraft.api.economy.Economy;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.util.ItemUtils;
import de.raidcraft.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author mdoering
 */
public final class ActionAPI {

    public enum GlobalActions {

        GIVE_ITEM("player.give.item", new Action<Player>() {
            @Override
            public void accept(Player player, ConfigurationSection config) {

                try {
                    ItemStack item = RaidCraft.getItem(config.getString("item"), config.getInt("amount", 1));
                    player.getInventory().addItem(item);
                } catch (CustomItemException e) {
                    RaidCraft.LOGGER.warning("player.give.item (" + player.getName() + "): " + e.getMessage());
                }
            }
        }),
        GIVE_MONEY("player.give.money", new Action<Player>() {
            @Override
            public void accept(Player player, ConfigurationSection config) {

                if (!config.isSet("amount")) return;
                Economy economy = RaidCraft.getEconomy();
                economy.add(AccountType.PLAYER, player.getUniqueId().toString(), economy.parseCurrencyInput(config.getString("amount")));
            }
        }),
        KILL_PLAYER("player.kill", (Player player, ConfigurationSection config) -> player.setHealth(0.0)),
        MESSAGE_PLAYER("player.message", new Action<Player>() {
            @Override
            public void accept(Player player, ConfigurationSection config) {

                String[] text = config.getString("text").split("|");
                for (String msg : text) {
                    player.sendMessage(msg);
                }
            }
        }),
        TOGGLE_DOOR("door.toggle", new DoorAction()),
        SPAWN_COMPASS("spawn.compass", (Player player, ConfigurationSection config) -> {
            ItemStack item = ItemUtils.createItem(Material.COMPASS, config.getString("name"));
            World world = Bukkit.getWorld(config.getString("world", player.getWorld().getName()));
            if (world == null) return;
            Location location = new Location(world,
                    config.getInt("x"),
                    config.getInt("y"),
                    config.getInt("z"));
            player.setCompassTarget(location);
            if (player.getInventory().addItem(item).size() != 0) {
                player.getWorld().dropItem(player.getLocation(), item);
            }
        }),
        SET_BLOCK("block.set", new SetBlockAction()),
        TELEPORT_COORDS("teleport.location", (Player player, ConfigurationSection config) -> {

            World world = Bukkit.getWorld(config.getString("world", player.getWorld().getName()));
            if (world == null) return;
            Location location = new Location(world,
                    config.getInt("x"),
                    config.getInt("y"),
                    config.getInt("z"),
                    (float) config.getDouble("yaw"),
                    (float) config.getDouble("pitch"));
            player.teleport(location);
        });

        private final String id;
        private final Action<?> action;

        <T> GlobalActions(String id, Action<T> action) {

            this.id = id;
            this.action = action;
        }

        public String getId() {

            return id;
        }

        public Action<?> getAction() {

            return action;
        }
    }

    public enum GlobalRequirements {

        IS_SPRINTING("player.is-sprinting", (Player player, ConfigurationSection config) -> player.isSprinting()),
        PLAYER_LOCATION("player.location", new Requirement<Player>() {
            @Override
            public boolean test(Player player, ConfigurationSection config) {

                Location location = player.getLocation();
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
        }),
        HAS_ITEM("player.has-item", (Player player, ConfigurationSection config) -> {

            try {
                ItemStack item = RaidCraft.getItem(config.getString("item"));
                return player.getInventory().contains(item);
            } catch (CustomItemException ignored) {
            }
            return false;
        });

        private final String id;
        private final Requirement<?> requirement;

        <T> GlobalRequirements(String id, Requirement<T> requirement) {

            this.id = id;
            this.requirement = requirement;
        }
    }

    public static void registerGlobalActions(ActionFactory factory) {

        for (GlobalActions globalActions : GlobalActions.values()) {
            factory.registerGlobalAction(globalActions.id, globalActions.action);
        }
    }

    public static void registerGlobalRequirements(RequirementFactory factory) {


        for (GlobalRequirements globalRequirement : GlobalRequirements.values()) {
            factory.registerGlobalRequirement(globalRequirement.id, globalRequirement.requirement);
        }
    }

    public static void registerGlobalTrigger(TriggerManager manager) {

        manager.registerGlobalTrigger(new PlayerTrigger());
    }

    public static String getIdentifier(Object object) {

        if (object instanceof Action) {
            return ActionFactory.getInstance().getActionIdentifier((Action<?>) object);
        } else if (object instanceof Requirement) {
            return RequirementFactory.getInstance().getRequirementIdentifier((Requirement<?>) object);
        } else if (object instanceof Trigger) {
            return ((Trigger) object).getIdentifier();
        }
        return "undefined";
    }
}

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

    public enum GlobalActions {

        GIVE_ITEM("player.give.item", new Action<Player>() {
            @Override
            public void accept(Player player) {

                try {
                    ItemStack item = RaidCraft.getItem(getConfig().getString("item"), getConfig().getInt("amount", 1));
                    player.getInventory().addItem(item);
                } catch (CustomItemException e) {
                    RaidCraft.LOGGER.warning("player.give.item (" + player.getName() + "): " + e.getMessage());
                }
            }
        }),
        KILL_PLAYER("player.kill", (Player player) -> player.setHealth(0.0)),
        MESSAGE_PLAYER("player.message", new Action<Player>() {
            @Override
            public void accept(Player player) {

                String[] text = getConfig().getString("text").split("|");
                for (String msg : text) {
                    player.sendMessage(msg);
                }
            }
        }),
        TOGGLE_DOOR("door.toggle", new DoorAction()),
        SET_BLOCK("block.set", new SetBlockAction()),
        TELEPORT_COORDS("teleport.coords", new Action<Player>() {
            @Override
            public void accept(Player player) {

                World world = Bukkit.getWorld(getConfig().getString("world", player.getWorld().getName()));
                if (world == null) return;
                Location location = new Location(world,
                        getConfig().getInt("x"),
                        getConfig().getInt("y"),
                        getConfig().getInt("z"),
                        (float) getConfig().getDouble("yaw"),
                        (float) getConfig().getDouble("pitch"));
                player.teleport(location);
            }
        }),
        TELEPORT_PLAYER("teleport.player", new Action<Player>() {
            @Override
            public void accept(Player player) {

                Player targetPlayer = Bukkit.getPlayer(getConfig().getString("player"));
                if (targetPlayer != null) player.teleport(targetPlayer);
            }
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

        IS_SPRINTING("player.is-sprinting", Player::isSprinting),
        PLAYER_LOCATION("player.location", new Requirement<Player>() {
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
        }),
        HAS_ITEM("player.has-item", new Requirement<Player>() {
            @Override
            public boolean test(Player player) {

                try {
                    ItemStack item = RaidCraft.getItem(getConfig().getString("item"));
                    return player.getInventory().contains(item);
                } catch (CustomItemException ignored) {
                }
                return false;
            }
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

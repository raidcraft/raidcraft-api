package de.raidcraft.api.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
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
import de.raidcraft.api.items.ItemType;
import de.raidcraft.api.quests.QuestProvider;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.util.CustomItemUtil;
import de.raidcraft.util.InventoryUtils;
import de.raidcraft.util.ItemUtils;
import de.raidcraft.util.LocationUtil;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

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
                    InventoryUtils.addOrDropItems(player, item);
                } catch (CustomItemException e) {
                    player.sendMessage(ChatColor.RED + e.getMessage());
                    RaidCraft.LOGGER.warning("player.give.item (" + player.getName() + "): " + e.getMessage());
                }
            }
        }),
        REMOVE_ITEM("player.remove.item", new Action<Player>() {
            @Override
            public void accept(Player player, ConfigurationSection config) {

                try {
                    ItemStack item = RaidCraft.getItem(config.getString("item"));
                    int amount = config.getInt("amount", 1);
                    do {
                        if (amount <= item.getMaxStackSize()) {
                            item.setAmount(amount);
                            amount = 0;
                        } else {
                            item.setAmount(item.getMaxStackSize());
                            amount -= item.getMaxStackSize();
                        }
                        if (CustomItemUtil.isCustomItem(item) && RaidCraft.getCustomItem(item).getItem().getType() == ItemType.QUEST) {
                            Optional<QuestProvider> questProvider = Quests.getQuestProvider();
                            if (questProvider.isPresent()) {
                                questProvider.get().removeQuestItem(player, item);
                            }
                        } else {
                            player.getInventory().removeItem(item);
                        }
                    } while (amount > 0);

                    player.getInventory().removeItem(item);

                } catch (CustomItemException e) {
                    player.sendMessage(ChatColor.RED + e.getMessage());
                    RaidCraft.LOGGER.warning("player.remove.item (" + player.getName() + "): " + e.getMessage());
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
	    TAKE_MONEY("player.take.money", new Action<Player>() {
		    @Override
		    public void accept(Player player, ConfigurationSection config) {

			    if (!config.isSet("amount")) return;
			    Economy economy = RaidCraft.getEconomy();
			    economy.substract(AccountType.PLAYER, player.getUniqueId().toString(), economy.parseCurrencyInput(config.getString("amount")));
		    }
	    }),
        KILL_PLAYER("player.kill", (Player player, ConfigurationSection config) -> player.damage(player.getMaxHealth() * 10)),
        MESSAGE_PLAYER("player.message", new Action<Player>() {
            @Override
            public void accept(Player player, ConfigurationSection config) {

                String[] text = config.getString("text").split("\\|");
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
            InventoryUtils.addOrDropItems(player, item);
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
	    IS_ALIVE("player.is-alive", (Player player, ConfigurationSection config) -> !player.isDead()),
        DUMMY("dummy", (Player player, ConfigurationSection config) -> true),
        EXECUTE_ONCE_TRIGGER("execute-once-trigger", new Requirement<Player>() {
            @Override
            public boolean test(Player type, ConfigurationSection config) {

                return !isChecked(type);
            }
        }),
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
                int amount = config.getInt("amount", 1);
                if (player.getInventory().containsAtLeast(item, amount)) {
                    return true;
                }
                Optional<QuestProvider> questProvider = Quests.getQuestProvider();
                if (questProvider.isPresent()) {
                    return questProvider.get().hasQuestItem(player, item, amount);
                }
            } catch (CustomItemException e) {
                e.printStackTrace();
            }
            return false;
        });

        private final String id;
        private final Requirement<?> requirement;

        <T> GlobalRequirements(String id, Requirement<T> requirement) {

            this.id = id;
            this.requirement = requirement;
        }

        public String getId() {

            return id;
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

    public static ActionAPI register(BasePlugin plugin) {

        return new ActionAPI(plugin);
    }

    private final ActionFactory actions;
    private final TriggerManager trigges;
    private final RequirementFactory requirements;
    private final BasePlugin plugin;
    private boolean global = false;

    private ActionAPI(BasePlugin plugin) {

        this.actions = ActionFactory.getInstance();
        this.trigges = TriggerManager.getInstance();
        this.requirements = RequirementFactory.getInstance();
        this.plugin = plugin;
    }

    public <T> ActionAPI action(@NonNull String identifier, @NonNull Action<T> action) {

        if (global) {
            actions.registerGlobalAction(identifier, action);
        } else {
            actions.registerAction(plugin, identifier, action);
        }
        return this;
    }

    public <T> ActionAPI requirement(@NonNull String identifier, @NonNull Requirement<T> requirement) {

        if (global) {
            requirements.registerGlobalRequirement(identifier, requirement);
        } else {
            requirements.registerRequirement(plugin, identifier, requirement);
        }
        return this;
    }

    public ActionAPI trigger(@NonNull Trigger trigger) {

        if (global) {
            trigges.registerGlobalTrigger(trigger);
        } else {
            trigges.registerTrigger(plugin, trigger);
        }
        return this;
    }

    public ActionAPI global() {

        global = true;
        return this;
    }

    public ActionAPI local() {

        global = false;
        return this;
    }
}

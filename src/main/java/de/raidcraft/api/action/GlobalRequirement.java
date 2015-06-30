package de.raidcraft.api.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.ContextualRequirement;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.quests.QuestProvider;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.util.ConfigUtil;
import de.raidcraft.util.LocationUtil;
import de.raidcraft.util.TimeUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

/**
 * @author mdoering
 */
@Getter
public enum GlobalRequirement {

    IS_SPRINTING("player.sprinting", (Player player, ConfigurationSection config) -> player.isSprinting()),
    IS_SNEAKING("player.sneaking", (Player player, ConfigurationSection config) -> player.isSneaking()),
    IS_ALIVE("player.alive", (Player player, ConfigurationSection config) -> !player.isDead()),
    DUMMY("dummy", (Player player, ConfigurationSection config) -> true),
    EXECUTE_ONCE_TRIGGER("execute-once-trigger", new ContextualRequirement<Player>() {
        @Override
        public boolean test(Player type, RequirementConfigWrapper<Player> context, ConfigurationSection config) {

            return !context.isChecked(type);
        }
    }),
    COOLDOWN("cooldown", new ContextualRequirement<Player>() {
        @Override
        @Information(
                value = "cooldown",
                desc = "When this requirement is checked the first time it will be true. Then for the duration of the cooldown " +
                        "it will be false and when a check occurs after the cooldown expired the requirement will be true again.",
                conf = "cooldown: <[1y]{200d][11h][3m][20s][10]>"
        )
        public boolean test(Player player, RequirementConfigWrapper<Player> context, ConfigurationSection config) {

            if (context.isMapped(player, "last_activation")) {
                Timestamp lastActivation = Timestamp.valueOf(context.getMapping(player, "last_activation"));
                long cooldown = config.isLong("cooldown") ? config.getLong("cooldown") : TimeUtil.parseTimeAsTicks(config.getString("cooldown"));
                cooldown = TimeUtil.ticksToMillis(cooldown);
                if (lastActivation.toInstant().plusMillis(cooldown).isAfter(Instant.now())) {
                    return false;
                }
            }
            context.setMapping(player, "last_activation", Timestamp.from(Instant.now()).toString());
            return true;
        }
    }),
    COOLDOWN("cooldown", new Requirement<Player>() {
        @Override
        @Information(
                value = "cooldown",
                desc = "When this requirement is checked the first time it will be true. Then for the duration of the cooldown " +
                        "it will be false and when a check occurs after the cooldown expired the requirement will be true again.",
                conf = "cooldown: in seconds"
        )
        public boolean test(Player player, ConfigurationSection config) {

            if (isMapped(player, "last_activation")) {
                Timestamp lastActivation = Timestamp.valueOf(getMapping(player, "last_activation"));
                if (lastActivation.toInstant().plusSeconds(config.getLong("cooldown")).isAfter(Instant.now())) {
                    return false;
                }
            }
            setMapping(player, "last_activation", Timestamp.from(Instant.now()).toString());
            return true;
        }
    }),
    PLAYER_LOCATION("player.location", new Requirement<Player>() {
        @Override
        @Information(
                value = "player.location",
                desc = "Checks if the player is at or in a radius of the given location.",
                conf = {
                        "world: [current]",
                        "x",
                        "y",
                        "z",
                        "radius: [0]"
                }
        )
        public boolean test(Player player, ConfigurationSection config) {

            Location location = player.getLocation();
            World world = Bukkit.getWorld(config.getString("world", player.getWorld().getName()));
            if (config.isSet("world") && world == null) return false;

            if (config.isSet("x") && config.isSet("y") && config.isSet("z") && config.isSet("world")) {
                if (config.isSet("radius")) {
                    return LocationUtil.isWithinRadius(location,
                            ConfigUtil.getLocationFromConfig(config, player),
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
        @Information(
                value = "player.has-item",
                desc = "Checks if the player has the given item in his (quest) inventory.",
                conf = {
                        "amount: [1]",
                        "item: <rc1337/so43034/world.quest.named-item/WOOD:5>"
                }
        )
        public boolean test(Player player, ConfigurationSection config) {

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
        }
    }),
    TIMER_ACTIVE("timer.active", (type, config) -> Timer.isActive(type, config.getString("id")));

    private final String id;
    private final Requirement<Player> requirement;

    GlobalRequirement(String id, Requirement<Player> requirement) {

        this.id = id;
        this.requirement = requirement;
    }
}

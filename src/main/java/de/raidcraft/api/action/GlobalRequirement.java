package de.raidcraft.api.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.ContextualRequirement;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.requirement.global.TagRequirement;
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

    IS_SPRINTING("player.sprinting", new Requirement<Player>() {
        @Override
        @Information(value = "player.sprinting", desc = "Checks if the player is currently sprinting.")
        public boolean test(Player player, ConfigurationSection config) {

            return player.isSprinting();
        }
    }), IS_SNEAKING("player.sneaking", new Requirement<Player>() {
        @Override
        @Information(value = "player.sneaking", desc = "Checks if the player is currently sneaking.")
        public boolean test(Player type, ConfigurationSection config) {

            return type.isSneaking();
        }
    }), IS_ALIVE("player.alive", new Requirement<Player>() {
        @Override
        @Information(value = "player.alive", desc = "Checks if the player is currently alive.")
        public boolean test(Player type, ConfigurationSection config) {

            return !type.isDead();
        }
    }), DUMMY("dummy", new Requirement<Player>() {
        @Override
        @Information(value = "dummy", desc = "Dummy requirement for counting and persistent markers.", aliases = {
                "count" })
        public boolean test(Player type, ConfigurationSection config) {

            return true;
        }
    }), EXECUTE_ONCE_TRIGGER("execute-once-trigger", new ContextualRequirement<Player>() {
        @Override
        @Information(value = "execute-once-trigger", desc = "Dummy requirement to track execute once triggers.")
        public boolean test(Player type, RequirementConfigWrapper<Player> context, ConfigurationSection config) {

            return !context.isChecked(type);
        }
    }), COOLDOWN("cooldown", new ContextualRequirement<Player>() {
        @Override
        @Information(value = "cooldown", desc = "When this requirement is checked the first time it will be true. Then for the duration of the cooldown "
                + "it will be false and when a check occurs after the cooldown expired the requirement will be true again.", conf = "cooldown: <[1y]{200d][11h][3m][20s][10]>")
        public boolean test(Player player, RequirementConfigWrapper<Player> context, ConfigurationSection config) {

            if (context.isMapped(player, "last_activation")) {
                Timestamp lastActivation = Timestamp.valueOf(context.getMapping(player, "last_activation"));
                long cooldown = config.isLong("cooldown") ? config.getLong("cooldown")
                        : TimeUtil.parseTimeAsTicks(config.getString("cooldown"));
                cooldown = TimeUtil.ticksToMillis(cooldown);
                if (lastActivation.toInstant().plusMillis(cooldown).isAfter(Instant.now())) {
                    return false;
                }
            }
            context.setMapping(player, "last_activation", Timestamp.from(Instant.now()).toString());
            return true;
        }
    }), PLAYER_LOCATION("player.location", new Requirement<Player>() {
        @Override
        @Information(value = "player.location", desc = "Checks if the player is at or in a radius of the given location.", conf = {
                "x", "y", "z", "world: [current]", "radius: [0]" })
        public boolean test(Player player, ConfigurationSection config) {

            Location location = player.getLocation();
            World world = Bukkit.getWorld(config.getString("world", player.getWorld().getName()));
            if (config.isSet("world") && world == null)
                return false;

            if (config.isSet("x") && config.isSet("y") && config.isSet("z") && config.isSet("world")) {
                if (config.isSet("radius")) {
                    return LocationUtil.isWithinRadius(location, ConfigUtil.getLocationFromConfig(config, player),
                            config.getInt("radius"));
                }
                return config.getInt("x") == location.getBlockX() && config.getInt("y") == location.getBlockY()
                        && config.getInt("z") == location.getBlockZ() && world.equals(location.getWorld());
            }
            return false;
        }
    }), HAS_ITEM("player.has-item", new Requirement<Player>() {
        @Override
        @Information(value = "player.has-item", desc = "Checks if the player has the given item in his (quest) inventory.", conf = {
                "item: <rc1337/so43034/world.quest.named-item/WOOD:5>", "amount: [1]" })
        public boolean test(Player player, ConfigurationSection config) {

            try {
                ItemStack item = RaidCraft.getSafeItem(config.getString("item"));
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
    }), TIMER_ACTIVE("timer.active", new Requirement<Player>() {
        @Override
        @Information(value = "timer.active", desc = "Checks if the player has an active timer with the id.", conf = {
                "id: unique id of the timer" })
        public boolean test(Player type, ConfigurationSection config) {

            return Timer.isActive(type, config.getString("id"));
        }
    }), PLAYER_TAG("player.tag", new TagRequirement());

    private final String id;
    private final Requirement<Player> requirement;

    GlobalRequirement(String id, Requirement<Player> requirement) {

        this.id = id;
        this.requirement = requirement;
    }
}

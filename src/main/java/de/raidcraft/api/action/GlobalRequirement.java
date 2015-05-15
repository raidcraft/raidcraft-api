package de.raidcraft.api.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.quests.QuestProvider;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.util.ConfigUtil;
import de.raidcraft.util.LocationUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
    EXECUTE_ONCE_TRIGGER("execute-once-trigger", new Requirement<Player>() {
        @Override
        public boolean test(Player type, ConfigurationSection config) {

            return !isChecked(type);
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
    });

    private final String id;
    private final Requirement<Player> requirement;

    GlobalRequirement(String id, Requirement<Player> requirement) {

        this.id = id;
        this.requirement = requirement;
    }
}

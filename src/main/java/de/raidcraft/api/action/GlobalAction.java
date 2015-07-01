package de.raidcraft.api.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.global.DoorAction;
import de.raidcraft.api.action.action.global.SetBlockAction;
import de.raidcraft.api.economy.AccountType;
import de.raidcraft.api.economy.Economy;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.items.ItemType;
import de.raidcraft.api.quests.QuestProvider;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.util.ConfigUtil;
import de.raidcraft.util.CustomItemUtil;
import de.raidcraft.util.InventoryUtils;
import de.raidcraft.util.ItemUtils;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * @author mdoering
 */
@Getter
public enum GlobalAction {

    PLAYER_FREEZE("player.freeze", new Action<Player>() {
        @Override
        @Information(
                value = "player.freeze",
                desc = "Freezes the given player in place making him unable to move. Needs to be disabled with player.unfreeze"
        )
        public void accept(Player player, ConfigurationSection config) {

            player.setWalkSpeed(0F);
            player.setFlySpeed(0F);
        }
    }),
    PLAYER_UNFREEZE("player.unfreeze", new Action<Player>() {
        @Override
        @Information(
                value = "player.unfreeze",
                desc = "Unfreezes the player resetting his walk and fly speed to the default values"
        )
        public void accept(Player player, ConfigurationSection config) {

            player.setWalkSpeed(DEFAULT_WALK_SPEED);
            player.setFlySpeed(DEFAULT_FLY_SPEED);
        }
    }),
    GIVE_ITEM("player.give.item", new Action<Player>() {
        @Override
        @Information(
                value = "player.give.item",
                desc = "Gives the player the item or drops it if inventory is full.",
                conf = {
                        "item: <rc1337/so43034/world.quest.named-item/WOOD:5>",
                        "amount: [1]"
                }
        )
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
        @Information(
                value = "player.remove.item",
                desc = "Removes the item from the player, will also search in the quest inventory if it is a quest item.",
                conf = {
                        "item: <rc1337/so43034/world.quest.named-item/WOOD:5>",
                        "amount: [1]"
                }
        )
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
        @Information(
                value = "player.give.money",
                desc = "Gives the player the given amount of money.",
                conf = {
                        "amount: <1g5s3k/1g/5g3k>|<50.0>"
                }
        )
        public void accept(Player player, ConfigurationSection config) {

            if (!config.isSet("amount")) return;
            Economy economy = RaidCraft.getEconomy();
            String amount = config.getString("amount");
            try {
                economy.add(AccountType.PLAYER, player.getUniqueId().toString(), Double.parseDouble(amount));
            } catch (NumberFormatException ignored) {
                economy.add(AccountType.PLAYER, player.getUniqueId().toString(), economy.parseCurrencyInput(amount));
            }
        }
    }),
    TAKE_MONEY("player.remove.money", new Action<Player>() {
        @Override
        @Information(
                value = "player.remove.money",
                desc = "Removes the given amount of money from the player.",
                conf = {
                        "amount: <1g5s3k/1g/5g3k>|<50.0>"
                }
        )
        public void accept(Player player, ConfigurationSection config) {

            if (!config.isSet("amount")) return;
            Economy economy = RaidCraft.getEconomy();
            String amount = config.getString("amount");
            try {
                economy.substract(AccountType.PLAYER, player.getUniqueId().toString(), Double.parseDouble(amount));
            } catch (NumberFormatException ignored) {
                economy.substract(AccountType.PLAYER, player.getUniqueId().toString(), economy.parseCurrencyInput(amount));
            }
        }
    }),
    KILL_PLAYER("player.kill", (Player player, ConfigurationSection config) -> player.damage(player.getMaxHealth() * 10)),
    MESSAGE_PLAYER("player.message", new Action<Player>() {
        @Override
        @Information(
                value = "player.message",
                desc = "Sends the given message to the player. Multiline splitting with |.",
                conf = {
                        "text: <First line.|Second line.>"
                }
        )
        public void accept(Player player, ConfigurationSection config) {

            String[] text = config.getString("text").split("\\|");
            for (String msg : text) {
                player.sendMessage(msg);
            }
        }
    }),
    TEXT("text", new Action<Player>() {
        @Override
        @Information(
                value = "text",
                desc = "Sends the given text to the player prepended by the given NPC name.",
                conf = {
                        "text: <First line.|Second line.>",
                        "npc: name"
                }
        )
        public void accept(Player player, ConfigurationSection config) {

            String[] text = config.getString("text").split("\\|");
            for (String line : text) {
                if (config.isSet("npc")) {
                    player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD
                            + config.getString("npc") + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + ": "
                            + ChatColor.AQUA + line
                    );
                } else {
                    player.sendMessage(ChatColor.AQUA + line);
                }
            }
        }
    }),
    TOGGLE_DOOR("door.toggle", new DoorAction()),
    GIVE_COMPASS("player.give.compass", new Action<Player>() {
        @Override
        @Information(
                value = "player.give.compass",
                desc = "Gives the player a compass that points to the given location and names it.",
                conf = {
                        "x",
                        "y",
                        "z",
                        "world: [current]",
                        "name: [Compass]"
                }
        )
        public void accept(Player player, ConfigurationSection config) {

            ItemStack item = ItemUtils.createItem(Material.COMPASS, config.getString("name", "Compass"));
            Location location = ConfigUtil.getLocationFromConfig(config, player);
            player.setCompassTarget(location);
            InventoryUtils.addOrDropItems(player, item);
        }
    }),
    REMOVE_COMPASS("player.remove.compass", new Action<Player>() {
        @Override
        @Information(
                value = "player.remove.compass",
                desc = "Removes the compass with the given name from the player.",
                conf = {
                        "name: [Compass]"
                }
        )
        public void accept(Player player, ConfigurationSection config) {

            String name = config.getString("name", "Compass");
            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null
                        && itemStack.hasItemMeta()
                        && itemStack.getItemMeta().getDisplayName() != null
                        && name.equals(itemStack.getItemMeta().getDisplayName())) {
                    player.getInventory().remove(itemStack);
                }
            }
        }
    }),
    SET_BLOCK("block.set", new SetBlockAction()),
    TELEPORT_COORDS("teleport.location", new Action<Player>() {
        @Override
        @Information(
                value = "teleport.location",
                desc = "Teleports the player to the given location.",
                conf = {
                        "x",
                        "y",
                        "z",
                        "world: [current]",
                        "yaw",
                        "pitch"
                }
        )
        public void accept(Player player, ConfigurationSection config) {

            player.teleport(ConfigUtil.getLocationFromConfig(config, player));
        }
    }),
    START_TIMER("timer.start", Timer::startTimer),
    ADD_TIMER_TIME("timer.add", (type, config) -> {
        Optional<Timer> timer = Timer.getActiveTimer(type, config.getString("id"));
        if (timer.isPresent()) {
            if (config.getBoolean("temporary", false)) {
                timer.get().addTemporaryTime(config.getDouble("time"));
            } else {
                timer.get().addTime(config.getDouble("time"));
            }
        }
    }),
    ABORT_TIMER("timer.cancel", (type, config) -> Timer.cancelTimer(type, config.getString("id"))),
    RESET_TIMER("timer.reset", (type, config) -> Timer.resetTimer(type, config.getString("id")));

    private static final float DEFAULT_WALK_SPEED = 0.1F;
    private static final float DEFAULT_FLY_SPEED = 0.05F;

    private final String id;
    private final Action<Player> action;

    GlobalAction(String id, Action<Player> action) {

        this.id = id;
        this.action = action;
    }
}

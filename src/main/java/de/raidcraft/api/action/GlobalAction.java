package de.raidcraft.api.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.global.DoorAction;
import de.raidcraft.api.action.action.global.PlayerTagAction;
import de.raidcraft.api.action.action.global.RemovePlayerTag;
import de.raidcraft.api.action.action.global.SetBlockAction;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.economy.AccountType;
import de.raidcraft.api.economy.Economy;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.items.ItemType;
import de.raidcraft.api.locations.Locations;
import de.raidcraft.api.quests.QuestProvider;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.util.*;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

/**
 * @author mdoering
 */
@Getter
public enum GlobalAction {

    PLAYER_FREEZE("player.freeze", new Action<Player>() {
        @Override
        @Information(value = "player.freeze", desc = "Freezes the given player in place making him unable to move. Needs to be disabled with player.unfreeze")
        public void accept(Player player, ConfigurationSection config) {

            player.setWalkSpeed(0F);
            player.setFlySpeed(0F);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128, false));
        }
    }), PLAYER_UNFREEZE("player.unfreeze", new Action<Player>() {
        @Override
        @Information(value = "player.unfreeze", desc = "Unfreezes the player resetting his walk and fly speed to the default values")
        public void accept(Player player, ConfigurationSection config) {

            player.setWalkSpeed(DEFAULT_WALK_SPEED);
            player.setFlySpeed(DEFAULT_FLY_SPEED);
            player.removePotionEffect(PotionEffectType.JUMP);
        }
    }), GIVE_ITEM("player.give.item", new Action<Player>() {
        @Override
        @Information(value = "player.give.item", desc = "Gives the player the item or drops it if inventory is full.", conf = {
                "item: <rc1337/so43034/world.quest.named-item/WOOD:5>",
                "amount: [1]"}, aliases = {"player.item.give", "item.give"})
        public void accept(Player player, ConfigurationSection config) {

            try {
                ItemStack item = RaidCraft.getSafeItem(config.getString("item"), config.getInt("amount", 1));
                InventoryUtils.addOrDropItems(player, item);
            } catch (CustomItemException e) {
                player.sendMessage(ChatColor.RED + e.getMessage());
                RaidCraft.LOGGER.warning("player.give.item (" + player.getName() + "): " + e.getMessage());
            }
        }
    }), REMOVE_ITEM("player.remove.item", new Action<Player>() {
        @Override
        @Information(value = "player.remove.item", desc = "Removes the item from the player, will also search in the quest inventory if it is a quest item.", conf = {
                "item: <rc1337/so43034/world.quest.named-item/WOOD:5>",
                "amount: [1]"}, aliases = {"player.item.remove", "item.remove"})
        public void accept(Player player, ConfigurationSection config) {

            try {
                ItemStack item = RaidCraft.getSafeItem(config.getString("item"));
                int amount = config.getInt("amount", 1);
                do {
                    if (amount <= item.getMaxStackSize()) {
                        item.setAmount(amount);
                        amount = 0;
                    } else {
                        item.setAmount(item.getMaxStackSize());
                        amount -= item.getMaxStackSize();
                    }
                    if (CustomItemUtil.isCustomItem(item)
                            && RaidCraft.getCustomItem(item).getItem().getType() == ItemType.QUEST) {
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
    }), GIVE_MONEY("player.give.money", new Action<Player>() {
        @Override
        @Information(value = "player.give.money", desc = "Gives the player the given amount of money.", conf = {
                "amount: <1g5s3k/1g/5g3k>|<50.0>"}, aliases = {"money.give", "money.add"})
        public void accept(Player player, ConfigurationSection config) {

            if (!config.isSet("amount"))
                return;
            Economy economy = RaidCraft.getEconomy();
            String amount = config.getString("amount");
            try {
                economy.add(AccountType.PLAYER, player.getUniqueId().toString(), Double.parseDouble(amount));
            } catch (NumberFormatException ignored) {
                economy.add(AccountType.PLAYER, player.getUniqueId().toString(), economy.parseCurrencyInput(amount));
            }
        }
    }), TAKE_MONEY("player.remove.money", new Action<Player>() {
        @Override
        @Information(value = "player.remove.money", desc = "Removes the given amount of money from the player.", conf = {
                "amount: <1g5s3k/1g/5g3k>|<50.0>"}, aliases = {"money.remove"})
        public void accept(Player player, ConfigurationSection config) {

            if (!config.isSet("amount"))
                return;
            Economy economy = RaidCraft.getEconomy();
            String amount = config.getString("amount");
            try {
                economy.substract(AccountType.PLAYER, player.getUniqueId().toString(), Double.parseDouble(amount));
            } catch (NumberFormatException ignored) {
                economy.substract(AccountType.PLAYER, player.getUniqueId().toString(),
                        economy.parseCurrencyInput(amount));
            }
        }
    }), KILL_PLAYER("player.kill", new Action<Player>() {
        @Override
        @Information(value = "player.kill", desc = "Kills the player by damaging him 10x the max health.")
        public void accept(Player player, ConfigurationSection config) {

            player.damage(player.getMaxHealth() * 10);
        }
    }), MESSAGE_PLAYER("player.message", new Action<Player>() {
        @Override
        @Information(value = "player.message", desc = "Sends the given message to the player. Multiline splitting with |.", conf = {
                "text: <First line.|Second line.>"})
        public void accept(Player player, ConfigurationSection config) {

            String[] text = config.getString("text").split("\\|");
            for (String msg : text) {
                player.sendMessage(RaidCraft.replaceVariables(player, msg));
            }
        }
    }), TEXT("text", new Action<Player>() {
        @Override
        @Information(value = "text", desc = "Sends the given text to the player prepended by the given NPC displayName.", conf = {
                "text: <First line.|Second line.>", "npc: displayName"})
        @SuppressWarnings("unchecked")
        public void accept(Player player, ConfigurationSection config) {

            Optional<Conversation> activeConversation = Conversations.getActiveConversation(player);
            String npc;
            if (activeConversation.isPresent()) {
                Optional<String> name = activeConversation.get().getHost().getName();
                npc = name.orElse(null);
            } else {
                npc = Conversations.getConversationHost(config.getString("npc"))
                        .map(ConversationHost::getName)
                        .map(name -> name.orElse(null))
                        .orElse(config.getString("npc"));
            }
            String[] text = config.getString("text").split("\\|");
            for (String line : text) {
                String message;
                if (npc != null) {
                    message = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + npc + ChatColor.DARK_GRAY + "]"
                            + ChatColor.GOLD + ": " + ChatColor.AQUA + line;
                } else {
                    message = ChatColor.AQUA + line;
                }
                if (activeConversation.isPresent()) {
                    activeConversation.get().sendMessage(message);
                } else {
                    player.sendMessage(RaidCraft.replaceVariables(player, message));
                }
            }
        }
    }), TEXT_PLAYER("text.player", new Action<Player>() {
        @Override
        @Information(value = "text.player", desc = "Sends the given text to the player prepended by the player displayName.", conf = {
                "text: <First line.|Second line.>"})
        public void accept(Player player, ConfigurationSection config) {

            String[] text = config.getString("text").split("\\|");
            for (String line : text) {
                line = RaidCraft.replaceVariables(player, line);
                player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + player.getName() + ChatColor.DARK_GRAY
                        + "]" + ChatColor.GOLD + ": " + ChatColor.AQUA + line);
            }
        }
    }), TEXT_INFO("text.info", new Action<Player>() {
        @Override
        @Information(value = "text.info", desc = "Sends the given text to the player formatted in DARK AQUA.", conf = {
                "text: <First line.|Second line.>"})
        public void accept(Player player, ConfigurationSection config) {

            String[] text = config.getString("text").split("\\|");
            for (String line : text) {
                line = RaidCraft.replaceVariables(player, line);
                player.sendMessage(ChatColor.DARK_AQUA + line);
            }
        }
    }), TEXT_THINK("text.think", new Action<Player>() {
        @Override
        @Information(value = "text.think", desc = "Sends the given text to the player formatted in GRAY ITALIC.", conf = {
                "text: <First line.|Second line.>"})
        public void accept(Player player, ConfigurationSection config) {

            String[] text = config.getString("text").split("\\|");
            for (String line : text) {
                line = RaidCraft.replaceVariables(player, line);
                player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + line);
            }
        }
    }), TOGGLE_DOOR("door.toggle", new DoorAction()), GIVE_COMPASS("player.give.compass", new Action<Player>() {
        @Override
        @Information(value = "player.give.compass", desc = "Gives the player a compass that points to the given location and names it.", conf = {
                "x", "y", "z", "world: [current]", "displayName: [Compass]"})
        public void accept(Player player, ConfigurationSection config) {

            ItemStack item = ItemUtils.createItem(Material.COMPASS, config.getString("name", "Compass"));
            Locations.fromConfig(config, player).ifPresent(location -> player.setCompassTarget(location.getLocation()));
            InventoryUtils.addOrDropItems(player, item);
        }
    }), REMOVE_COMPASS("player.remove.compass", new Action<Player>() {
        @Override
        @Information(value = "player.remove.compass", desc = "Removes the compass with the given displayName from the player.", conf = {
                "displayName: [Compass]"})
        public void accept(Player player, ConfigurationSection config) {

            String name = config.getString("name", "Compass");
            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().getDisplayName() != null
                        && name.equals(itemStack.getItemMeta().getDisplayName())) {
                    player.getInventory().remove(itemStack);
                }
            }
        }
    }), PLAYER_HEAL("player.heal", new Action<Player>() {
        @Override
        @Information(value = "player.heal", desc = "Heals the player by the given amount or to max.", conf = {
                "amount: defaults to max", "from-max: false"}, aliases = {"heal"})
        public void accept(Player player, ConfigurationSection config) {

            if (!config.isSet("amount")) {
                player.setHealth(player.getMaxHealth());
                return;
            }
            double amount;
            if (config.getBoolean("from-max")) {
                amount = player.getHealth() + player.getMaxHealth() * config.getDouble("amount");
            } else {
                amount = player.getHealth() + config.getDouble("amount");
            }
            player.setHealth(amount);
        }
    }), SET_BLOCK("block.set", new SetBlockAction()), CHANGE_WORLD("world.change", new Action<Player>() {
        @Override
        @Information(value = "world.change", desc = "Teleports the player to the given world but keeps the exact position.", conf = "world")
        public void accept(Player player, ConfigurationSection config) {

            World world = Bukkit.getWorld(config.getString("world"));
            if (world == null) {
                RaidCraft.LOGGER
                        .warning("Invalid world in world.change action defined! " + ConfigUtil.getFileName(config));
                return;
            }
            Location location = player.getLocation();
            location.setWorld(world);
            player.teleport(location);
        }
    }), TELEPORT_COORDS("teleport.location", new Action<Player>() {
        @Override
        @Information(value = "teleport.location", aliases = {"teleport", "player.teleport", "tp"}, desc = "Teleports the player to the given location.", conf = {"x",
                "y", "z", "world: [current]", "yaw", "pitch"})
        public void accept(Player player, ConfigurationSection config) {

            Locations.fromConfig(config, player).ifPresent(location -> player.teleport(location.getLocation()));
        }
    }), START_TIMER("timer.start", new Action<Player>() {
        @Override
        @Information(value = "timer.start", desc = "Starts a timer for the player, executing the given actions at cancel or end.", conf = {
                "id: unique id for the timer", "duration: 10s2 -> 10secs 2 ticks",
                "end-actions: block of actions that are executed when the timer ends - you can also use the timer.end trigger",
                "cancel-actions: block of actions that are executed when the timer is cancelled - you can also use the timer.cancel trigger",
                "type: [interval] - defaults to a normal timer that runs out", "delay: interval mode only",
                "interval: interval mode only - use the timer.tick trigger"})
        public void accept(Player type, ConfigurationSection config) {

            Timer.startTimer(type, config);
        }
    }), ADD_TIMER_TIME("timer.add", new Action<Player>() {
        @Override
        @Information(value = "timer.add", desc = "Adds time to a running timer.", conf = {"id: unique id of the timer",
                "time: 10s2 -> 10secs 2 ticks to add",
                "temporary: true/<false> - calling timer.add multiple times with temporary true will not add up"})
        public void accept(Player type, ConfigurationSection config) {

            Optional<Timer> timer = Timer.getActiveTimer(type, config.getString("id"));
            if (timer.isPresent()) {
                if (config.getBoolean("temporary", false)) {
                    timer.get().addTemporaryTime(TimeUtil.parseTimeAsTicks(config.getString("time")));
                } else {
                    timer.get().addTime(TimeUtil.parseTimeAsTicks(config.getString("time")));
                }
            }
        }
    }), ABORT_TIMER("timer.cancel", new Action<Player>() {
        @Override
        @Information(value = "timer.cancel", desc = "Cancels the given timer calling the timer.cancel trigger.", conf = {
                "id: unique id of the timer"})
        public void accept(Player type, ConfigurationSection config) {

            Timer.cancelTimer(type, config.getString("id"));
        }
    }), END_TIMER("timer.end", new Action<Player>() {
        @Override
        @Information(value = "timer.end", desc = "Ends the timer calling the trigger timer.end", conf = {
                "id: unique id of the timer"})
        public void accept(Player type, ConfigurationSection config) {

            Timer.endTimer(type, config.getString("id"));
        }
    }), RESET_TIMER("timer.reset", new Action<Player>() {
        @Override
        @Information(value = "timer.reset", desc = "Resets the given timer cancelling it and then starting it again. Will also trigger timer.cancel!", conf = {
                "id: unique id of the timer"})
        public void accept(Player type, ConfigurationSection config) {

            Timer.resetTimer(type, config.getString("id"));
        }
    }), PLAYER_TAG("player.tag", new PlayerTagAction()),
    PLAYER_TAG_REMOVE("player.tag.remove", new RemovePlayerTag());

    private static final float DEFAULT_WALK_SPEED = 0.1F * 2.0F;
    private static final float DEFAULT_FLY_SPEED = 0.05F * 2.0F;

    private final String id;
    private final Action<Player> action;

    GlobalAction(String id, Action<Player> action) {

        this.id = id;
        this.action = action;
    }
}

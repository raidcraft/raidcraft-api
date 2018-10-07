package de.raidcraft;

import com.google.common.base.Strings;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.api.bukkit.BukkitPlayer;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationVariable;
import de.raidcraft.api.conversations.conversation.PlayerVariable;
import de.raidcraft.api.conversations.legacy.ConversationProvider;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.database.Table;
import de.raidcraft.api.economy.Economy;
import de.raidcraft.api.hero.DefaultHeroProvider;
import de.raidcraft.api.hero.HeroProvider;
import de.raidcraft.api.inventory.InvalidInventoryException;
import de.raidcraft.api.inventory.InventoryManager;
import de.raidcraft.api.inventory.PersistentInventory;
import de.raidcraft.api.items.*;
import de.raidcraft.api.items.attachments.ItemAttachmentManager;
import de.raidcraft.api.items.attachments.ItemAttachmentProvider;
import de.raidcraft.api.permissions.GroupManager;
import de.raidcraft.api.permissions.RCPermissionsProvider;
import de.raidcraft.api.player.PlayerComponent;
import de.raidcraft.api.player.PlayerStatisticProvider;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.api.quests.QuestProvider;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.api.storage.ItemStorage;
import de.raidcraft.api.storage.StorageException;
import de.raidcraft.api.trades.TradeProvider;
import de.raidcraft.util.*;
import io.ebean.EbeanServer;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the static class that gives access to all important API
 * methods and components.
 *
 * @author Silthus
 */
public class RaidCraft implements Listener {


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (players.containsKey(event.getPlayer().getName())) {
            players.get(event.getPlayer().getName()).destroy();
            players.remove(event.getPlayer().getName());
        }
    }

    /*///////////////////////////////////////////////////////////////////////////
     //                      RaidCraft RCRPG Utility Class
     ///////////////////////////////////////////////////////////////////////////*/

    public static final Logger LOGGER = Logger.getLogger("Minecraft.RaidCraft");
    public static final String CUSTOM_ITEM_IDENTIFIER = "rc";
    public static final String STORED_OBJECT_IDENTIFIER = "so";

    private static final Map<UUID, RCPlayer> players = new HashMap<>();
    private static final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private static final Map<String, PlayerStatisticProvider> statisticProviders = new CaseInsensitiveMap<>();
    private static final Map<Pattern, PlayerVariable> playerVariables = new HashMap<>();
    private static Economy economy;
    private static ConversationProvider conversationProvider;
    private static TradeProvider tradeProvider;
    @Setter
    @Getter
    private static GroupManager permissionGroupManager;
    @Getter
    private static RCPermissionsProvider permissionsProvider;
    private static HeroProvider heroProvider = new DefaultHeroProvider();

    /**
     * Gets the wrapped Player for interaction with the player and his surroundings.
     *
     * @param name of the player
     *
     * @return RCPlayer
     */
    @Deprecated
    public static RCPlayer safeGetPlayer(String name) throws UnknownPlayerException {

        Player player = Bukkit.getPlayer(name);
        RCPlayer rcPlayer = null;

        if (player == null) {
            OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(name);
            if (offlinePlayer != null && offlinePlayer.hasPlayedBefore()) {
                // add the player to cache if he isnt added
                if (players.containsKey(offlinePlayer.getUniqueId())) {
                    rcPlayer = players.get(offlinePlayer.getUniqueId());
                } else {
                    rcPlayer = new BukkitPlayer(offlinePlayer.getName());
                    players.put(offlinePlayer.getUniqueId(), rcPlayer);
                }
            }
        } else {
            rcPlayer = getPlayer(player);
        }
        if (rcPlayer == null) throw new UnknownPlayerException("Es gibt keinen Spieler mit dem Namen: " + name);
        return rcPlayer;
    }

    /**
     * Gets the player but will ignore the exception thrown.
     * This can be used when you know that the player displayName is correct.
     *
     * @param name of the player
     *
     * @return wrapped RCPlayer object
     */
    @Deprecated
    public static RCPlayer getPlayer(String name) {

        try {
            return safeGetPlayer(name);
        } catch (UnknownPlayerException e) {
            LOGGER.severe(e.getMessage());
            //            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the player from the Bukkit Player object.
     *
     * @param player object from bukkit
     *
     * @return RCPlayer
     */
    public static RCPlayer getPlayer(Player player) {

        RCPlayer rcPlayer;
        if (players.containsKey(player.getUniqueId())) {
            rcPlayer = players.get(player.getUniqueId());
        } else {
            rcPlayer = new BukkitPlayer(player);
            players.put(player.getUniqueId(), rcPlayer);
        }
        return rcPlayer;
    }

    public static boolean hasMoved(Player player, Location location) {

        return getPlayer(player).hasMoved(location);
    }

    public static void registerPlayerVariable(Pattern pattern, PlayerVariable variable) {

        playerVariables.put(pattern, variable);
    }

    public static Map<Pattern, PlayerVariable> getPlayerVariables() {

        return playerVariables;
    }

    public static String replaceVariables(Player player, String message) {

        Optional<Conversation> activeConversation = Conversations.getActiveConversation(player);
        if (activeConversation.isPresent()) {
            for (Map.Entry<Pattern, ConversationVariable> entry : Conversations.getConversationVariables().entrySet()) {
                Matcher matcher = entry.getKey().matcher(message);
                if (matcher.find()) {
                    String replacement = entry.getValue().replace(matcher, activeConversation.get());
                    if (replacement != null) {
                        message = matcher.replaceAll(replacement);
                    }
                }
            }
        }
        for (Map.Entry<Pattern, PlayerVariable> entry : RaidCraft.getPlayerVariables().entrySet()) {
            Matcher matcher = entry.getKey().matcher(message);
            if (matcher.find()) {
                String replacement = entry.getValue().replace(matcher, player);
                if (replacement != null) {
                    message = matcher.replaceAll(replacement);
                }
            }
        }
        return message;
    }

    /**
     * Gets the registered table of the given table class.
     *
     * @param tClass of the table
     * @param <T>    table object
     *
     * @return table
     */
    public static <T extends Table> T getTable(Class<T> tClass) {

        return Database.getTable(tClass);
    }

    /**
     * Gets the given PlayerComponent for the given Player object.
     *
     * @param player to get component for
     * @param clazz  of the component
     * @param <T>    component type
     *
     * @return PlayerComponent
     */
    public static <T extends PlayerComponent> T getPlayerComponent(RCPlayer player, Class<T> clazz) {

        return player.getComponent(clazz);
    }

    /**
     * Registers the given component with the RaidCraft API making it usable.
     *
     * @param clazz     to register
     * @param component instance
     */
    public static void registerComponent(Class<? extends Component> clazz, Component component) {

        components.put(clazz, component);
    }

    /**
     * Unregisters the given component class.
     *
     * @param clazz to unregister
     */
    public static void unregisterComponent(Class<? extends Component> clazz) {

        components.remove(clazz);
    }

    /**
     * Gets the given component instance.
     *
     * @param clazz of the component
     * @param <T>   type
     *
     * @return casted component instance
     */
    public static <T extends Component> T getComponent(Class<T> clazz) {

        return clazz.cast(components.get(clazz));
    }

    public static <T extends Event> void callEvent(T event) {

        Bukkit.getPluginManager().callEvent(event);
    }

    public static void setupEconomy(Economy e) {

        RaidCraft.economy = e;
    }

    public static Economy getEconomy() {

        return economy;
    }

    public static ConversationProvider getConversationProvider() {

        return conversationProvider;
    }

    public static void setupConversationProvider(ConversationProvider conversationProvider) {

        RaidCraft.conversationProvider = conversationProvider;
    }

    public static TradeProvider getTradeProvider() {

        return tradeProvider;
    }

    public static void setupTradeProvider(TradeProvider tradeProvider) {

        RaidCraft.tradeProvider = tradeProvider;
    }

    public static Permission getPermissions() {

        return getComponent(RaidCraftPlugin.class).getPermission();
    }

    public static Chat getChat() {

        return getComponent(RaidCraftPlugin.class).getChat();
    }


    public static WorldGuardPlugin getWorldGuard() {

        Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if (worldGuard != null) {
            return (WorldGuardPlugin) worldGuard;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <V> V getMetaData(Metadatable metadatable, MetaDataKey key, V def) {

        List<MetadataValue> metadata = metadatable.getMetadata(key.getKey());
        if (metadata == null || metadata.size() < 1) return def;
        for (MetadataValue value : metadata) {
            if (value.getOwningPlugin().equals(RaidCraft.getComponent(RaidCraftPlugin.class))) {
                return (V) value.value();
            }
        }
        return def;
    }

    public static <V> void setMetaData(Metadatable metadatable, MetaDataKey key, V value) {

        metadatable.setMetadata(key.getKey(), new FixedMetadataValue(RaidCraft.getComponent(RaidCraftPlugin.class), value));
    }

    public static void removeMetaData(Metadatable metadatable, MetaDataKey key) {

        metadatable.removeMetadata(key.getKey(), RaidCraft.getComponent(RaidCraftPlugin.class));
    }

    public static boolean isPlayerPlacedBlock(Block block) {

        BlockTracker component = RaidCraft.getComponent(BlockTracker.class);
        if (component == null) return false;

        return component.isPlayerPlacedBlock(block);
    }

    public static EbeanServer getDatabase(Class<? extends BasePlugin> clazz) {

        return RaidCraft.getComponent(clazz).getRcDatabase();
    }

    public static CustomItemStack getCustomItem(CustomItem customItem) {

        return getComponent(CustomItemManager.class).getCustomItemStack(customItem);
    }

    public static CustomItemStack getCustomItem(ItemStack itemStack) {

        return getComponent(CustomItemManager.class).getCustomItem(itemStack);
    }

    public static CustomItem getCustomItem(String name) {

        if (name.startsWith(CUSTOM_ITEM_IDENTIFIER)) name = name.replaceFirst(CUSTOM_ITEM_IDENTIFIER, "");
        Optional<CustomItem> customItem = getComponent(CustomItemManager.class).getCustomItem(name);
        if (customItem.isPresent()) return customItem.get();
        return null;
    }

    public static CustomItem getCustomItem(int id) throws CustomItemException {

        return getComponent(CustomItemManager.class).getCustomItem(id);
    }

    public static CustomItemStack getCustomItemStack(int id) throws CustomItemException {

        return getComponent(CustomItemManager.class).getCustomItemStack(id);
    }

    public static boolean isCustomItem(ItemStack itemStack) {

        return CustomItemUtil.isCustomItem(itemStack);
    }

    /**
     * Checks if the given {@link ItemStack} is of type {@link ItemType#QUEST}.
     *
     * @param item to check
     * @return true if item is a quest item
     */
    public static boolean isQuestItem(ItemStack item) {
        if (RaidCraft.isCustomItem(item)) {
            CustomItemStack customItem = RaidCraft.getCustomItem(item);
            return customItem.getItem().getType() == ItemType.QUEST;
        }
        return false;
    }

    /**
     * Parses the given Item String and tries to resolve it as one of the given items:
     * - Custom Item: rci[0-9]+, e.g. rci1337
     * - Stored Item: so[0-9]+, eg. so1234
     * - Skull: skull:Silthus
     * - Vanilla Item: name:data, e.g. STICK:5
     * <p>
     * All items can be created with a specified amount (defaults to 1) by appending the amount after a pound sign.
     * e.g. STICK#10 will create 10 sticks.
     *
     * @param id of the item to parse
     * @return created item or an empty optional
     */
    public static Optional<ItemStack> getItem(String id) {

        if (Strings.isNullOrEmpty(id)) return Optional.empty();

        try {
            String[] split = id.toLowerCase().split("#");
            id = split[0];
            int amount = split.length > 1 ? Integer.parseInt(split[1]) : 1;
            ItemStack itemStack = null;

            if (id.startsWith(CUSTOM_ITEM_IDENTIFIER)) {
                itemStack = getCustomItemStack(Integer.parseInt(id.replace(CUSTOM_ITEM_IDENTIFIER, "")));
            } else if (id.startsWith(STORED_OBJECT_IDENTIFIER)) {
                itemStack = new ItemStorage("API").getObject(Integer.parseInt(id.replace(STORED_OBJECT_IDENTIFIER, "")));
            } else {
                // check if it is a named custom item
                CustomItemManager itemManager = RaidCraft.getComponent(CustomItemManager.class);
                if (itemManager != null) {
                    Optional<ItemStack> customItemStack = itemManager.getCustomItem(id).map(CustomItem::createNewItem).map(item -> {
                        item.setAmount(amount);
                        return item;
                    });
                    if (customItemStack.isPresent()) return customItemStack;
                }
                // fallback to vanilla items
                Material item = ItemUtils.getItem(id);
                String[] data = id.split(":");
                if (item == null) return Optional.empty();
                if ((item == Material.SKULL_ITEM || item == Material.SKULL) && data.length > 1) {
                    itemStack = Skull.getSkull(data[1]);
                } else {
                    if (data.length > 1) {
                        itemStack = new ItemStack(item, amount, Short.parseShort(data[1]));
                    } else {
                        itemStack = new ItemStack(item);
                    }
                }
            }
            itemStack.setAmount(amount);
            return Optional.of(itemStack);
        } catch (CustomItemException | NumberFormatException | StorageException e) {
            LOGGER.warning(e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Will try to parse any item out of the given id. The result can be a custom item or stored item.
     * Even minecraft Items are possible.
     *
     * @param id of the item
     *
     * @return created itemstack out of the id
     *
     * @throws CustomItemException is thrown if nothing matched
     */
    public static ItemStack getSafeItem(String id) throws CustomItemException {
        return getItem(id).orElseThrow(() -> new CustomItemException("Unknown item with id " + id));
    }


    public static ItemStack getSafeItem(String id, int amount) throws CustomItemException {

        return getItem(id).map(itemStack -> {
            itemStack.setAmount(amount);
            return itemStack;
        }).orElseThrow(() -> new CustomItemException("Unknown item with id " + id));
    }

    @Nullable
    public static ItemStack getUnsafeItem(String id) {

        return getItem(id).orElse(null);
    }

    @Nullable
    public static ItemStack getUnsafeItem(String id, int amount) {

        ItemStack item = getUnsafeItem(id);
        if (item != null) {
            item.setAmount(amount);
            return item;
        }
        return null;
    }

    public static String getItemIdString(ItemStack itemStack) {

        return getItemIdString(itemStack, false);
    }

    public static String getItemIdString(ItemStack itemStack, boolean storeObject) {

        if (itemStack == null) return null;

        StringBuilder sb = new StringBuilder();

        // lets try some stuff and see what item type this is
        if (CustomItemUtil.isCustomItem(itemStack)) {
            CustomItemStack customItem = getCustomItem(itemStack);
            if (customItem != null) {
                sb.append(CUSTOM_ITEM_IDENTIFIER).append(customItem.getItem().getId());
            }
        } else if (storeObject
                && ((itemStack.hasItemMeta() && (itemStack.getItemMeta().hasDisplayName() || itemStack.getItemMeta().hasLore()))
                || itemStack.getType() == Material.WRITTEN_BOOK
                || itemStack.getType() == Material.ENCHANTED_BOOK
                || itemStack.getType() == Material.BOOK_AND_QUILL
                || !itemStack.getEnchantments().isEmpty())) {
            // lets check this param after the custom item, but before mc
            sb.append(STORED_OBJECT_IDENTIFIER).append(new ItemStorage("API").storeObject(itemStack));
        } else {
            sb.append(itemStack.getType().name()).append(":").append(itemStack.getDurability());
        }

        if (itemStack.getAmount() > 1) {
            sb.append("#").append(itemStack.getAmount());
        }

        return sb.toString();
    }

    public static void removeStoredItem(String id) {
        try {
            if (Strings.isNullOrEmpty(id) || !id.startsWith(STORED_OBJECT_IDENTIFIER)) return;
            int itemId = Integer.parseInt(id.toLowerCase().replace(STORED_OBJECT_IDENTIFIER, ""));
            new ItemStorage("API").removeObject(itemId);
        } catch (StorageException ignored) {
        }
    }

    public static void registerItemAttachmentProvider(ItemAttachmentProvider provider) throws RaidCraftException {

        RaidCraft.getComponent(ItemAttachmentManager.class).registerItemAttachmentProvider(provider);
    }

    public static PersistentInventory getInventory(int id) throws InvalidInventoryException {

        return RaidCraft.getComponent(InventoryManager.class).getInventory(id);
    }

    public static PersistentInventory createInventory(Inventory inventory) {

        return RaidCraft.getComponent(InventoryManager.class).createInventory(inventory);
    }

    public static PersistentInventory createInventory(String title, int size) {

        return RaidCraft.getComponent(InventoryManager.class).createInventory(title, size);
    }

    public static void registerEvents(Listener listener, Plugin plugin) {

        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    /**
     * Intern Rc Log, saved into rc_log table
     */
    public static void info(String message, String category) {

        RaidCraft.LOGGER.info(message);
    }

    public static void log(String message, String category, Level level) {

        RaidCraft.LOGGER.log(level, message);
    }

    public static void registerPlayerStatisticProvider(BasePlugin plugin, String name, PlayerStatisticProvider provider) {

        statisticProviders.put(plugin.getName() + "." + name, provider);
    }

    public static Map<String, PlayerStatisticProvider> getStatisticProviders() {

        return statisticProviders;
    }

    public static PlayerStatisticProvider getStatisticProvider(String name) {

        return statisticProviders.get(name);
    }

    public static void registerHeroProvider(HeroProvider provider) {

        heroProvider = provider;
    }

    public static void registerPermissionsProvider(RCPermissionsProvider provider) {
        permissionsProvider = provider;
    }

    public static HeroProvider getHeroProvider() {
        return heroProvider;
    }
}

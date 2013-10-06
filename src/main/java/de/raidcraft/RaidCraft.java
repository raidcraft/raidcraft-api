package de.raidcraft;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.api.bukkit.BukkitPlayer;
import de.raidcraft.api.conversations.ConversationProvider;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.database.Table;
import de.raidcraft.api.economy.Economy;
import de.raidcraft.api.inventory.InvalidInventoryException;
import de.raidcraft.api.inventory.InventoryManager;
import de.raidcraft.api.inventory.PersistentInventory;
import de.raidcraft.api.items.CustomItem;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.items.CustomItemManager;
import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.api.items.Skull;
import de.raidcraft.api.items.attachments.ItemAttachmentManager;
import de.raidcraft.api.items.attachments.ItemAttachmentProvider;
import de.raidcraft.api.player.PlayerComponent;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.api.storage.ItemStorage;
import de.raidcraft.api.storage.StorageException;
import de.raidcraft.util.CustomItemUtil;
import de.raidcraft.util.ItemUtils;
import de.raidcraft.util.MetaDataKey;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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

    private static final Map<String, RCPlayer> players = new HashMap<>();
    private static final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private static Economy economy;
    private static ConversationProvider conversationProvider;

    /**
     * Gets the wrapped Player for interaction with the player and his surroundings.
     *
     * @param name of the player
     *
     * @return RCPlayer
     */
    public static RCPlayer safeGetPlayer(String name) throws UnknownPlayerException {

        Player player = Bukkit.getPlayer(name);
        RCPlayer rcPlayer = null;

        if (player == null) {
            OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(name);
            if (offlinePlayer != null && offlinePlayer.hasPlayedBefore()) {
                // add the player to cache if he isnt added
                if (players.containsKey(offlinePlayer.getName())) {
                    rcPlayer = players.get(offlinePlayer.getName());
                } else {
                    rcPlayer = new BukkitPlayer(offlinePlayer.getName());
                    players.put(offlinePlayer.getName(), rcPlayer);
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
     * This can be used when you know that the player name is correct.
     *
     * @param name of the player
     *
     * @return wrapped RCPlayer object
     */
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
        if (players.containsKey(player.getName())) {
            rcPlayer = players.get(player.getName());
        } else {
            rcPlayer = new BukkitPlayer(player);
            players.put(player.getName(), rcPlayer);
        }
        return rcPlayer;
    }

    public static boolean hasMoved(Player player, Location location) {

        return getPlayer(player).hasMoved(location);
    }

    /**
     * Gets the registered table of the given table class.
     *
     * @param tClass of the table
     * @param <T> table object
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

    public static Permission getPermissions() {

        return RaidCraftPlugin.getInstance().getPermissions();
    }

    public static Chat getChat() {

        return RaidCraftPlugin.getInstance().getChat();
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

    public static void setPlayerPlacedBlock(Block block) {

        RaidCraft.getComponent(RaidCraftPlugin.class).setPlayerPlaced(block);
    }

    public static boolean isPlayerPlacedBlock(Block block) {

        return RaidCraft.getComponent(RaidCraftPlugin.class).isPlayerPlaced(block);
    }

    public static void removePlayerPlacedBlock(Block block) {

        RaidCraft.getComponent(RaidCraftPlugin.class).removePlayerPlaced(block);
    }

    public static EbeanServer getDatabase(Class<? extends BasePlugin> clazz) {

        return RaidCraft.getComponent(clazz).getDatabase();
    }

    public static CustomItemStack getCustomItem(CustomItem customItem) {

        return getComponent(CustomItemManager.class).getCustomItemStack(customItem);
    }

    public static CustomItemStack getCustomItem(ItemStack itemStack) {

        return getComponent(CustomItemManager.class).getCustomItem(itemStack);
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
     * Will try to parse any item out of the given id. The result can be a custom item or stored item.
     * Even minecraft Items are possible.
     *
     * @param id of the item
     * @return created itemstack out of the id
     * @throws CustomItemException is thrown if nothing matched
     */
    public static ItemStack getItem(String id) throws CustomItemException {

        if (id == null || id.equals("")) return null;
        try {
            String lowercaseId = id.toLowerCase();
            if (lowercaseId.startsWith(CUSTOM_ITEM_IDENTIFIER)) {
                // its a custom item
                return getCustomItemStack(Integer.parseInt(lowercaseId.replace(CUSTOM_ITEM_IDENTIFIER, "")));
            } else if (lowercaseId.startsWith(STORED_OBJECT_IDENTIFIER)) {
                // its a stored item object
                return new ItemStorage("API").getObject(Integer.parseInt(lowercaseId.replace(STORED_OBJECT_IDENTIFIER, "")));
            } else {
                // its a minecraft item
                Material item = ItemUtils.getItem(lowercaseId);
                if (item != null && (item == Material.SKULL_ITEM || item == Material.SKULL)) {
                    return Skull.getSkull(id);
                } else if (item != null) {
                    return new ItemStack(item, 1, ItemUtils.getItemData(lowercaseId));
                }
            }
        } catch (StorageException | IndexOutOfBoundsException | NumberFormatException e) {
            throw new CustomItemException(e.getMessage());
        }
        throw new CustomItemException("Unknown item type specified: " + id);
    }

    public static ItemStack getUnsafeItem(String id) {

        try {
            return getItem(id);
        } catch (CustomItemException e) {
            return null;
        }
    }

    public static String getItemIdString(ItemStack itemStack) {

        return getItemIdString(itemStack, false);
    }

    public static String getItemIdString(ItemStack itemStack, boolean storeObject) {

        // lets try some stuff and see what item type this is
        if (CustomItemUtil.isCustomItem(itemStack)) {
            return CUSTOM_ITEM_IDENTIFIER + getCustomItem(itemStack).getItem().getId();
        }
        // lets check this param after the custom item, but before mc
        if (storeObject || (itemStack.hasItemMeta() && (itemStack.getItemMeta().hasDisplayName() || itemStack.getItemMeta().hasLore()))) {
            return STORED_OBJECT_IDENTIFIER + new ItemStorage("RaidCraft-API").storeObject(itemStack);
        }
        // so nothing matched :( bukkit here ya go!
        return itemStack.getType().name() + ":" + itemStack.getDurability();
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
}

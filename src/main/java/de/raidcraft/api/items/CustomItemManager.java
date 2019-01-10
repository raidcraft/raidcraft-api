package de.raidcraft.api.items;

import com.sk89q.util.StringUtil;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.CustomItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public final class CustomItemManager implements Component {

    // custom item id | custom item
    private final Map<Integer, CustomItem> customItems = new HashMap<>();
    private final Map<Integer, Gem> loadedGems = new HashMap<>();
    // item alias | item id
    private final Map<String, Integer> customItemAliases = new CaseInsensitiveMap<>();
    private final Map<String, CustomItem> namedCustomItems = new CaseInsensitiveMap<>();
    // minecraft item id | possible list of custom items
    private final Map<Material, List<CustomItem>> mappedMinecraftItems = new HashMap<>();

    public CustomItemStack getCustomItemStack(CustomItem customItem) {

        ItemStack itemStack = new ItemStack(customItem.getMinecraftItem(), 1, customItem.getMinecraftDataValue());
        return new CustomItemStack(customItem, itemStack);
    }

    public CustomItemStack getCustomItem(ItemStack itemStack) {

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return null;
        }
        if (itemStack.hasItemMeta()) {
            try {
                int id = CustomItemUtil.decodeItemId(itemStack.getItemMeta());
                try {
                    CustomItem item;
                    if (id == CustomItem.NAMED_CUSTOM_ITEM_ID) {
                        item = findCustomItem(itemStack.getItemMeta().getDisplayName());
                    } else {
                        item = getCustomItem(id);
                    }
                    return new CustomItemStack(item, itemStack);
                } catch (CustomItemException e) {
                    // if we have a valid id it is a valid "custom item" but
                    // this exception got thrown so we dont have a database entry
                    // this means we need to strip the lore of the item
                    itemStack.setItemMeta(null);
                }
            } catch (CustomItemException ignored) {
            }
        }
        if (mappedMinecraftItems.containsKey(itemStack.getType())) {
            for (CustomItem customItem : mappedMinecraftItems.get(itemStack.getType())) {
                if (customItem.matches(itemStack)) {
                    return new CustomItemStack(customItem, itemStack);
                }
            }
        }
        return null;
    }

    public CustomItem getCustomItem(int id) throws CustomItemException {

        if (!customItems.containsKey(id)) {
            throw new CustomItemException("Es gibt kein Custom Item mit der ID: " + id);
        }
        return customItems.get(id);
    }

    public CustomItemStack getCustomItemStack(int id) throws CustomItemException {

        CustomItem customItem = customItems.get(id);
        if (customItem != null) {
            return customItem.createNewItem();
        }
        throw new CustomItemException("Unknown custom item with the id: " + id);
    }

    public Optional<CustomItem> getCustomItem(String name) {

        try {
            return Optional.ofNullable(getCustomItem(Integer.parseInt(name)));
        } catch (NumberFormatException | CustomItemException ignored) {
            String lowerCaseName = name.toLowerCase();
            if (namedCustomItems.containsKey(lowerCaseName)) {
                return Optional.ofNullable(namedCustomItems.get(lowerCaseName));
            } else if (customItemAliases.containsKey(lowerCaseName)) {
                try {
                    return Optional.ofNullable(getCustomItem(customItemAliases.get(lowerCaseName)));
                } catch (CustomItemException e) {
                    RaidCraft.LOGGER.warning("Invalid custom item alias matching: " + name + " -> " + customItemAliases.get(name) + " is not a valid item id.");
                    return Optional.empty();
                }
            }
            return customItems.values().stream().filter(item -> item.getName().equalsIgnoreCase(lowerCaseName)).findFirst();
        }
    }

    public CustomItem findCustomItem(String name) throws CustomItemException {

        try {
            return getCustomItem(Integer.parseInt(name));
        } catch (NumberFormatException e) {
            // first we need to strip the displayName of any special chars
            name = ChatColor.stripColor(name).toLowerCase();
            // lets check our named custom items first
            if (customItemAliases.containsKey(name)) {
                return getCustomItem(customItemAliases.get(name));
            } else if (namedCustomItems.containsKey(name)) {
                return namedCustomItems.get(name);
            }
            // okay nothing there, so lets search for a matching displayName
            List<CustomItem> matching = new ArrayList<>();
            Collection<CustomItem> customItems = new ArrayList<>(this.customItems.values());
            customItems.addAll(namedCustomItems.values());
            for (CustomItem item : customItems) {
                if (ChatColor.stripColor(item.getName()).equalsIgnoreCase(name)) {
                    return item;
                } else if (item.getName().toLowerCase().contains(name)) {
                    matching.add(item);
                }
            }
            if (matching.isEmpty()) {
                throw new CustomItemException("Es gibt kein Custom Item mit dem Namen: " + name);
            }
            if (matching.size() > 1) {
                List<String> names = new ArrayList<>(matching.size());
                for(CustomItem item : matching) {
                    names.add(item.getName());
                }
                throw new CustomItemException("Es gibt mehrere Custom Items mit dem Namen " + name + ": " +
                        StringUtil.joinString(names, ", ", 0));
            }
            return matching.get(0);
        }
    }

    public CustomItemStack getCustomItemStack(String name) throws CustomItemException {

        CustomItem customItem = findCustomItem(name);
        return customItem.createNewItem();
    }

    public void registerCustomItem(CustomItem item) throws DuplicateCustomItemException {

        if (customItems.containsKey(item.getId())) {
            throw new DuplicateCustomItemException("The custom item with the id " + item.getId() + " is already registered.");
        }
        customItems.put(item.getId(), item);
        if (!mappedMinecraftItems.containsKey(item.getMinecraftItem())) {
            mappedMinecraftItems.put(item.getMinecraftItem(), new ArrayList<>());
        }
        mappedMinecraftItems.get(item.getMinecraftItem()).add(item);
    }

    public void registerNamedCustomItem(String name, CustomItem item) throws DuplicateCustomItemException {

        if (namedCustomItems.containsKey(name)) {
            throw new DuplicateCustomItemException("The custom item with the displayName" + name + " is already registered.");
        }
        namedCustomItems.put(name, item);
    }

    public void unregisterNamedCustomItem(String name) {
        unregisterNamedCustomItem(name, false);
    }

    public void unregisterNamedCustomItem(String name, boolean deep) {
        if (deep) {
            namedCustomItems.keySet().stream()
                    .filter(key -> key.startsWith(name))
                    .collect(Collectors.toList())
                    .forEach(namedCustomItems::remove);
        } else {
            namedCustomItems.remove(name);
        }
    }

    public void registerCustomItemAlias(int id, String alias) throws DuplicateCustomItemException {

        if (customItemAliases.containsKey(alias) || namedCustomItems.containsKey(alias)) {
            throw new DuplicateCustomItemException("A named custom item with the id " + alias + " already exists.");
        }

        this.customItemAliases.put(alias, id);
    }

    public CustomItem unregisterCustomItem(int id) {

        CustomItem item = customItems.remove(id);
        if (item != null) {
            if (mappedMinecraftItems.containsKey(item.getMinecraftItem())) {
                mappedMinecraftItems.get(item.getMinecraftItem()).remove(item);
            }
        }
        return item;
    }

    public List<CustomItem> getLoadedCustomItems() {

        return new ArrayList<>(customItems.values());
    }

    public Optional<Gem> getGem(int id) {

        return Optional.ofNullable(loadedGems.get(id));
    }
}
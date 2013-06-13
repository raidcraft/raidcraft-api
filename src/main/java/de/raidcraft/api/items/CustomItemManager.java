package de.raidcraft.api.items;

import com.sk89q.util.StringUtil;
import de.raidcraft.api.Component;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public final class CustomItemManager implements Component {

    // custom item id | custom item
    private final Map<Integer, CustomItem> customItems = new HashMap<>();
    // minecraft item id | possible list of custom items
    private final Map<Integer, List<CustomItem>> mappedMinecraftItems = new HashMap<>();

    public CustomItemStack getCustomItem(ItemStack itemStack) {

        if (itemStack == null || itemStack.getTypeId() == 0) {
            return null;
        }
        if (mappedMinecraftItems.containsKey(itemStack.getTypeId())) {
            for (CustomItem customItem : mappedMinecraftItems.get(itemStack.getTypeId())) {
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

    public CustomItem getCustomItem(String name) throws CustomItemException {

        try {
            return getCustomItem(Integer.parseInt(name));
        } catch (NumberFormatException e) {
            name = name.toLowerCase();
            List<CustomItem> matching = new ArrayList<>();
            for (CustomItem item : customItems.values()) {
                if (item.getName().equalsIgnoreCase(name)) {
                    return item;
                } else if (item.getName().toLowerCase().contains(name)) {
                    matching.add(item);
                }
            }
            if (matching.isEmpty()) {
                throw new CustomItemException("Es gibt kein Custom Item mit dem Namen: " + name);
            }
            if (matching.size() > 1) {
                throw new CustomItemException("Es gibt mehrere Custom Items mit dem Namen " + name + ": " +
                        StringUtil.joinString(matching, ", ", 0));
            }
            return matching.get(0);
        }
    }

    public CustomItemStack getCustomItemStack(String name) throws CustomItemException {

        CustomItem customItem = getCustomItem(name);
        return customItem.createNewItem();
    }

    public void registerCustomItem(CustomItem item) throws DuplicateCustomItemException {

        if (customItems.containsKey(item.getId())) {
            throw new DuplicateCustomItemException("The custom item with the id " + item.getId() + " is already registered.");
        }
        customItems.put(item.getId(), item);
        if (!mappedMinecraftItems.containsKey(item.getMinecraftId())) {
            mappedMinecraftItems.put(item.getMinecraftId(), new ArrayList<CustomItem>());
        }
        mappedMinecraftItems.get(item.getMinecraftId()).add(item);
    }

    public CustomItem unregisterCustomItem(int id) {

        CustomItem item = customItems.remove(id);
        if (item != null) {
            if (mappedMinecraftItems.containsKey(item.getMinecraftId())) {
                mappedMinecraftItems.get(item.getMinecraftId()).remove(item);
            }
        }
        return item;
    }
}

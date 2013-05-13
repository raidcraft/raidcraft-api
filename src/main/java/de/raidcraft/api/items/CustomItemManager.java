package de.raidcraft.api.items;

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

    public CustomItem getCustomItem(int id) {

        return customItems.get(id);
    }

    public CustomItemStack getCustomItemStack(int id) {

        CustomItem customItem = customItems.get(id);
        if (customItem != null) {
            return customItem.createNewItem();
        }
        return null;
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

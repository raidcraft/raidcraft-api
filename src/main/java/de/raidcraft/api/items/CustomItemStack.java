package de.raidcraft.api.items;

import de.raidcraft.util.CustomItemUtil;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Silthus
 */
public class CustomItemStack {

    private final CustomItem item;
    private final ItemStack itemStack;
    private int durability;

    protected CustomItemStack(CustomItem item, ItemStack itemStack) {

        this.item = item;
        this.itemStack = itemStack;
        if (item instanceof CustomEquipment) {
            this.durability = ((CustomEquipment) item).parseDurability(itemStack);
        }
    }

    public int getDurability() {

        return durability;
    }

    public void setDurability(int durability) {

        if (durability < 1) {
            durability = 0;
        }
        this.durability = durability;
        if (getItem() instanceof CustomEquipment) {
            ((CustomEquipment) getItem()).updateDurability(getHandle(), durability);
        }
    }

    public int getMaxDurability() {

        if (getItem() instanceof CustomEquipment) {
            return ((CustomEquipment) getItem()).getMaxDurability();
        }
        return getDurability();
    }

    public CustomItem getItem() {

        return item;
    }

    public ItemStack getHandle() {

        return itemStack;
    }

    public void setMetaDataId(int id) {

        List<String> lore = getHandle().getItemMeta().getLore();
        try {
            CustomItemUtil.decodeItemId(lore.get(lore.size() - 1));
            // he had an id so replace the last entry
            lore.remove(lore.size() - 1);
        } catch (CustomItemException ignored) {
        }
        lore.add(CustomItemUtil.encodeItemId(id));
        getHandle().getItemMeta().setLore(lore);
    }

    public int getMetaDataId() {

        List<String> lore = getHandle().getItemMeta().getLore();
        try {
            return CustomItemUtil.decodeItemId(lore.get(lore.size() - 1));
        } catch (CustomItemException ignored) {
            return -1;
        }
    }

    public void rebuild() {

        getItem().rebuild(getHandle());
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof CustomItemStack)) return false;

        CustomItemStack that = (CustomItemStack) o;

        return (getMetaDataId() > 0 ? that.getMetaDataId() == getMetaDataId() : item.equals(that.item));
    }

    @Override
    public int hashCode() {

        return (getMetaDataId() > 0 ? getMetaDataId() : item.hashCode());
    }
}

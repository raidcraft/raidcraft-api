package de.raidcraft.api.items;

import de.raidcraft.util.CustomItemUtil;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Silthus
 */
public class CustomItemStack extends ItemStack {

    private final CustomItem item;
    private int durability;

    protected CustomItemStack(CustomItem item, ItemStack itemStack) {

        super(itemStack);
        this.item = item;
        if (item instanceof CustomEquipment) {
            this.durability = ((CustomEquipment) item).parseDurability(itemStack);
        }
        // lets remove the vanilla attributes to clean up the display
        new Attributes(this).clear();
    }

    public int getCustomDurability() {

        return durability;
    }

    public void setDurability(int durability) {

        if (durability < 1) {
            durability = 0;
        }
        this.durability = durability;
        if (getItem() instanceof CustomEquipment) {
            ((CustomEquipment) getItem()).updateDurability(this, durability);
        }
    }

    public int getMaxDurability() {

        if (getItem() instanceof CustomEquipment) {
            return ((CustomEquipment) getItem()).getMaxDurability();
        }
        return getCustomDurability();
    }

    public CustomItem getItem() {

        return item;
    }

    @Override
    public int getMaxStackSize() {

        return (getMetaDataId() > 0 ? 1 : getItem().getMaxStackSize());
    }

    public void setMetaDataId(int id) {

        List<String> lore = getItemMeta().getLore();
        try {
            CustomItemUtil.decodeItemId(lore.get(lore.size() - 1));
            // he had an id so replace the last entry
            lore.remove(lore.size() - 1);
        } catch (CustomItemException ignored) {
        }
        lore.add(CustomItemUtil.encodeItemId(id));
        getItemMeta().setLore(lore);
    }

    public int getMetaDataId() {

        List<String> lore = getItemMeta().getLore();
        try {
            return CustomItemUtil.decodeItemId(lore.get(lore.size() - 1));
        } catch (CustomItemException ignored) {
            return -1;
        }
    }

    public void rebuild() {

        getItem().rebuild(this);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof CustomItemStack)) return false;

        CustomItemStack that = (CustomItemStack) o;

        return (getMetaDataId() > 0 ? that.getMetaDataId() == getMetaDataId() : item.equals(that.item));
    }

    @Override
    public CustomItemStack clone() {

        return new CustomItemStack(getItem(), super.clone());
    }
}

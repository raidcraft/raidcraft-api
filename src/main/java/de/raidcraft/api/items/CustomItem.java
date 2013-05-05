package de.raidcraft.api.items;

import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
public interface CustomItem {

    public int getId();

    public String getEncodedId();

    public int getMinecraftId();

    public short getMinecraftDataValue();

    public String getName();

    public String getLore();

    public int getItemLevel();

    public ItemQuality getQuality();

    public double getSellPrice();

    public boolean matches(ItemStack itemStack);

    public boolean rebuild(ItemStack itemStack);

    public CustomItemStack createNewItem();
}

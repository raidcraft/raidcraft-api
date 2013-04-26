package de.raidcraft.api.items;

import de.raidcraft.api.requirement.RequirementResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
public interface CustomItem extends RequirementResolver<Player> {

    public int getId();

    public int getMinecraftId();

    public short getMinecraftDataValue();

    public String getName();

    public int getItemLevel();

    public ItemQuality getQuality();

    public double getSellPrice();

    public boolean matches(ItemStack itemStack);

    public CustomItemStack createNewItem();
}

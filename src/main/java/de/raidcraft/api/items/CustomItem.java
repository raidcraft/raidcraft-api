package de.raidcraft.api.items;

import de.raidcraft.api.items.tooltip.Tooltip;
import de.raidcraft.api.items.tooltip.TooltipSlot;
import de.raidcraft.api.requirement.RequirementResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * @author Silthus
 */
public interface CustomItem extends RequirementResolver<Player> {

    public int getId();

    public String getEncodedId();

    public int getMinecraftId();

    public short getMinecraftDataValue();

    public String getName();

    public String getLore();

    public void setItemLevel(int itemLevel);

    public int getItemLevel();

    public void setTradeable(boolean tradeable);

    public boolean isTradeable();

    public ItemQuality getQuality();

    public int getMaxStackSize();

    public double getSellPrice();

    public Tooltip getTooltip(TooltipSlot slot);

    public Map<TooltipSlot, Tooltip> getTooltips();

    public boolean matches(ItemStack itemStack);

    public CustomItemStack createNewItem();
}

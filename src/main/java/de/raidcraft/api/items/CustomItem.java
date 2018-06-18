package de.raidcraft.api.items;

import de.raidcraft.api.items.tooltip.Tooltip;
import de.raidcraft.api.items.tooltip.TooltipSlot;
import de.raidcraft.api.requirement.RequirementResolver;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public interface CustomItem extends RequirementResolver<Player> {

    int NAMED_CUSTOM_ITEM_ID = 0;
    int DYNAMIC_CUSTOM_ITEM_ID = 10;

    int getId();

    String getEncodedId();

    Material getMinecraftItem();

    short getMinecraftDataValue();

    String getName();

    String getLore();

    Set<ItemCategory> getCategories();

    void addCategory(ItemCategory category);

    void setItemLevel(int itemLevel);

    int getItemLevel();

    boolean isEnchantmentEffect();

    ItemType getType();

    ItemBindType getBindType();

    ItemQuality getQuality();

    int getMaxStackSize();

    double getSellPrice();

    boolean isBlockingUsage();

    boolean isLootable();

    Tooltip getTooltip(TooltipSlot slot);

    Map<TooltipSlot, Tooltip> getTooltips();

    boolean matches(ItemStack itemStack);

    CustomItemStack createNewItem();
}

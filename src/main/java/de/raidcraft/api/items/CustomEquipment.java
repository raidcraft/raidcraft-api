package de.raidcraft.api.items;

import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * @author Silthus
 */
public interface CustomEquipment extends CustomItem {

    public EquipmentSlot getEquipmentSlot();

    public Set<ItemAttribute> getAttributes();

    public int getMaxDurability();

    public int parseDurability(ItemStack itemStack);

    public void updateDurability(ItemStack itemStack, int durability);
}

package de.raidcraft.api.items.tooltip;

import de.raidcraft.api.items.ItemAttribute;

/**
 * @author mdoering
 */
public class EnchantmentTooltip extends AttributeTooltip {

    public EnchantmentTooltip(ItemAttribute... enchantments) {

        super(TooltipSlot.ENCHANTMENTS, enchantments);
    }
}

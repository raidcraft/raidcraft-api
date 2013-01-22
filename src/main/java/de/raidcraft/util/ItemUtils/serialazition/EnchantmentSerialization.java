package de.raidcraft.util.ItemUtils.serialazition;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Philip
 */
public class EnchantmentSerialization extends SimpleSerialization {

    public EnchantmentSerialization(ItemStack item) {
        super(item);
    }

    @Override
    public String serialize() {
        String enchantmentString = "";
        for (Map.Entry<Enchantment, Integer> enchantment : getItem().getEnchantments().entrySet()) {
            enchantmentString += enchantment.getKey().getName() + ":" + enchantment.getValue() + "|";
        }
        return enchantmentString;
    }

    @Override
    public ItemStack deserialize(String serializedData) {
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        String[] enchantmentPairs = serializedData.split("\\|");
        for (String enchantmentPair : enchantmentPairs) {
            String[] enchantmentPairSplit = enchantmentPair.split(":");
            try {
                enchantments.put(Enchantment.getByName(enchantmentPairSplit[0]), Integer.parseInt(enchantmentPairSplit[1]));
            } catch (Exception e) {
            }
        }

        getItem().addEnchantments(enchantments);
        return getItem();
    }
}

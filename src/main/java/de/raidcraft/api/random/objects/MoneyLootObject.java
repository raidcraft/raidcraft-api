package de.raidcraft.api.random.objects;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.economy.BalanceSource;
import de.raidcraft.api.random.Dropable;
import de.raidcraft.api.random.GenericRDSValue;
import de.raidcraft.api.random.Obtainable;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSObjectFactory;
import de.raidcraft.util.CustomItemUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author mdoering
 */
public class MoneyLootObject extends GenericRDSValue<Double> implements Obtainable, Dropable {

    @RDSObjectFactory.Name("money")
    public static class MoneyLootFactory implements RDSObjectFactory {

        @Override
        public RDSObject createInstance(ConfigurationSection config) {

            return new MoneyLootObject(config.getDouble("amount", 0), config.getString("reason"));
        }

    }
    private final String reason;

    public MoneyLootObject(double amount, String reason) {

        super(amount, 1);
        this.reason = reason;
    }

    @Override
    public void addTo(Player player) {

        if (getValue().isPresent() && getValue().get() != 0) {
            RaidCraft.getEconomy().add(player.getUniqueId(), getValue().get(), BalanceSource.LOOT_OBJECT, reason);
        }
    }

    @Override
    public ItemStack getItemStack() {

        if (!getValue().isPresent()) {
            return new ItemStack(Material.AIR);
        }
        ItemStack itemStack = new ItemStack(Material.GOLD_NUGGET, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(CustomItemUtil.getSellPriceString(getValue().get()));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public void pickup(Player player) {

        addTo(player);
    }
}

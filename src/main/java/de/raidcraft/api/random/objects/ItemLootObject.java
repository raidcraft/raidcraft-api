package de.raidcraft.api.random.objects;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItem;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.api.random.*;
import de.raidcraft.util.ConfigUtil;
import de.raidcraft.util.CustomItemUtil;
import de.raidcraft.util.InventoryUtils;
import de.raidcraft.util.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author mdoering
 */
public class ItemLootObject extends GenericRDSValue<ItemStack> implements RDSObjectCreator, Obtainable, Dropable, Spawnable {

    @RDSObjectFactory.Name("item")
    public static class ItemLootFactory implements RDSObjectFactory {

        @Override
        public RDSObject createInstance(ConfigurationSection config) {

            try {
                ItemLootObject lootObject = new ItemLootObject(RaidCraft.getSafeItem(config.getString("item"), config.getInt("amount", 1)));
                lootObject.setMinAmount(config.getInt("min-amount", -1));
                lootObject.setMaxAmount(config.getInt("max-amount", -1));
                lootObject.setPrice(RaidCraft.getEconomy().parseCurrencyInput(config.getString("price", "0")));
                return lootObject;
            } catch (CustomItemException e) {
                RaidCraft.LOGGER.warning("Invalid item " + config.getString("item") + " in loot-table " + ConfigUtil.getFileName(config));
                return new RDSNullValue(0);
            }
        }

    }

    @Getter
    @Setter
    private double price;
    @Getter
    @Setter
    private int minAmount = -1;
    @Getter
    @Setter
    private int maxAmount = -1;

    public ItemLootObject(String item, int amount) throws CustomItemException {

        this(RaidCraft.getSafeItem(item, amount));
    }

    public ItemLootObject(String item, int amount, double price) throws CustomItemException {

        this(RaidCraft.getSafeItem(item, amount));
        this.price = price;
    }

    public ItemLootObject(ItemStack itemStack) {

        this(itemStack, itemStack instanceof CustomItemStack ? ((CustomItemStack) itemStack).getItem().getSellPrice() : 0);
    }

    public ItemLootObject(ItemStack itemStack, double price) {

        super(itemStack);
        this.price = price;
    }

    public ItemLootObject(CustomItem customItem, double price) {

        super(customItem.createNewItem());
        this.price = price;
    }

    public ItemLootObject(CustomItem customItem) {

        this(customItem, customItem.getSellPrice());
    }

    @Override
    public RDSObject createInstance() {

        if (!getValue().isPresent()) {
            return new GenericRDSValue<>();
        }
        ItemStack itemStack = getValue().get().clone();
        if (getMinAmount() > -1 && getMaxAmount() > -1) {
            itemStack.setAmount(RDSRandom.getIntValue(getMinAmount(), getMaxAmount()));
        }
        return new ItemLootObject(itemStack, price);
    }

    @Override
    public void addTo(Player player) {

        if (getValue().isPresent()) {
            InventoryUtils.addOrDropItems(player, getValue().get());
        }
    }

    @Override
    public ItemStack getItemStack() {

        if (getValue().isPresent()) {
            return getValue().get();
        }
        return new ItemStack(Material.AIR);
    }

    @Override
    public void pickup(Player player) {

        addTo(player);
    }

    @Override
    public void spawn(Location location) {

        location.getWorld().dropItemNaturally(location, getItemStack());
    }
}

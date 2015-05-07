package de.raidcraft.api.random.objects;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItem;
import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.api.random.Dropable;
import de.raidcraft.api.random.GenericRDSValue;
import de.raidcraft.api.random.Obtainable;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSObjectCreator;
import de.raidcraft.api.random.RDSObjectFactory;
import de.raidcraft.api.random.Spawnable;
import de.raidcraft.util.InventoryUtils;
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

            if (config.isSet("price")) {
                return new ItemLootObject(config.getString("item"), config.getInt("amount", 1), config.getDouble("price"));
            } else {
                return new ItemLootObject(config.getString("item"), config.getInt("amount", 1));
            }
        }

    }

    @Getter
    @Setter
    private double price;

    public ItemLootObject(String item, int amount) {

        this(RaidCraft.getUnsafeItem(item, amount));
    }

    public ItemLootObject(String item, int amount, double price) {

        this(RaidCraft.getUnsafeItem(item, amount));
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
        return new ItemLootObject(getValue().get().clone(), price);
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

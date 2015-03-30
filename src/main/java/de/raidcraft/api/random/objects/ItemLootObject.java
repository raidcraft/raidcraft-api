package de.raidcraft.api.random.objects;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItem;
import de.raidcraft.api.random.Dropable;
import de.raidcraft.api.random.GenericRDSValue;
import de.raidcraft.api.random.Obtainable;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSObjectCreator;
import de.raidcraft.api.random.RDSObjectFactory;
import de.raidcraft.util.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author mdoering
 */
public class ItemLootObject extends GenericRDSValue<ItemStack> implements RDSObjectCreator, Obtainable, Dropable {

    @RDSObjectFactory.Name("item")
    public static class ItemLootFactory implements RDSObjectFactory {

        @Override
        public RDSObject createInstance(ConfigurationSection config) {

            return new ItemLootObject(config.getString("item"), config.getInt("amount", 1));
        }

    }

    public ItemLootObject(String item, int amount) {

        this(RaidCraft.getUnsafeItem(item, amount));
    }

    public ItemLootObject(ItemStack itemStack) {

        super(itemStack);
    }

    public ItemLootObject(CustomItem customItem) {

        super(customItem.createNewItem());
    }

    @Override
    public RDSObject createInstance() {

        if (!getValue().isPresent()) {
            return new GenericRDSValue<>();
        }
        return new ItemLootObject(getValue().get().clone());
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
}

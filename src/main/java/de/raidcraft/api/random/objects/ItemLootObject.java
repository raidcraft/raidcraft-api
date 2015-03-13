package de.raidcraft.api.random.objects;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.random.GenericRDSValue;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSObjectCreator;
import de.raidcraft.api.random.RDSObjectFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

/**
 * @author mdoering
 */
public class ItemLootObject extends GenericRDSValue<ItemStack> implements RDSObjectCreator {

    @RDSObjectFactory.Name("item")
    public static class ItemLootFactory implements RDSObjectFactory {

        @Override
        public RDSObject createInstance(ConfigurationSection config) {

            return new ItemLootObject(config.getString("item"), config.getInt("amount", 1));
        }
    }

    public ItemLootObject(String item, int amount) {

        super(RaidCraft.getUnsafeItem(item, amount).get());
    }

    @Override
    public RDSObject createInstance() {

        if (!getValue().isPresent()) {
            return new GenericRDSValue<>();
        }
        ItemStack itemStack = getValue().get();
        return new ItemLootObject(RaidCraft.getItemIdString(itemStack), itemStack.getAmount());
    }
}

package de.raidcraft.api.chestui.menuitems;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Dragonfire
 */
public class MenuItemInteractive extends MenuItemAPI {

    private ItemStack item_empty;
    private ItemStack item_full;
    @Getter
    private int maxAmount;
    private boolean empty = true;

    public MenuItemInteractive(ItemStack item_empty, ItemStack item_full,
                               int startAmount, int maxAmount) {

        this.item_empty = item_empty;
        setItem(item_empty);
        this.item_full = item_full;
        this.maxAmount = maxAmount;
        setAmount(startAmount);

    }

    public void decrease() {

        setAmount(getAmount() - 1);

    }

    public void increase() {

        setAmount(getAmount() + 1);

    }

    public void delta(int delta) {

        setAmount(getAmount() + delta);
    }

    public int getAmount() {

        return (empty) ? 0 : getItem().getAmount();
    }

    public void setAmount(int new_amount) {

        // if 0 or negative set empty item
        if (new_amount < 1) {
            setItem(item_empty);
            empty = true;
            return;
        }

        // check if max amount reached
        if (new_amount > maxAmount) {
            new_amount = maxAmount;
        }

        // if was empty set diret full item
        if (empty) {
            item_full.setAmount(new_amount);
            setItem(item_full);
        } else {
            getItem().setAmount(new_amount);
        }
    }

    @Override
    public void trigger(Player player) {
        // standard nothing
    }
}

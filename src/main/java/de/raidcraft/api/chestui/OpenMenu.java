package de.raidcraft.api.chestui;

import de.raidcraft.api.chestui.menuitems.MenuItemAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Dragonfire
 */
public class OpenMenu extends MenuItemAPI {

    private Menu menu;

    public OpenMenu(Menu menu) {

        this.menu = menu;
    }

    @Override
    public ItemStack getItem() {

        return new ItemStack(Material.FENCE_GATE);
    }

    @Override
    public void setItem(ItemStack item) {
        //TODO: implement
    }

    @Override
    public void trigger(Player player) {

        ChestUI.getInstance().openMenu(player, this.menu);
    }
}

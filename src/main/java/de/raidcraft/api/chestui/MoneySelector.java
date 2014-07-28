package de.raidcraft.api.chestui;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.chestui.menuitems.MenuItemAPI;
import de.raidcraft.api.chestui.menuitems.MenuItemInteractive;
import de.raidcraft.api.items.RC_Items;
import de.raidcraft.util.MathUtil;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Select a positive money value in a chest ui,
 * max 999 Gold, 99 Silver, ...
 * ugly but works
 *
 * @author Dragonfire
 */
public class MoneySelector {

    private static MoneySelector INSTANCE;
    private Plugin plugin;

    private MoneySelector() {

        this.plugin = RaidCraft.getComponent(RaidCraftPlugin.class);
    }

    public static MoneySelector getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new MoneySelector();
        }
        return INSTANCE;
    }

    public void openMoneySelection(final Player player, String menu_name, double currentMoneyValue,
                                   final MoneySelectorListener listener) {

        if (currentMoneyValue < 0) {
            RaidCraft.LOGGER.warning("MoneySelector: invalid money value: " + currentMoneyValue);
            return;
        }
        final int[] values = MathUtil.getDigits(currentMoneyValue, 2);

        Menu menu = new Menu(menu_name);


        // GGG SS KK
        final MenuItemInteractive g100 = new MenuItemInteractive(RC_Items.createItem(
                Material.GOLD_INGOT, ""),
                RC_Items.getGlassPane(DyeColor.YELLOW),
                1, 9);
        final MenuItemInteractive g10 = new MenuItemInteractive(RC_Items.createItem(
                Material.GOLD_INGOT, ""),
                RC_Items.getGlassPane(DyeColor.YELLOW),
                1, 9);
        final MenuItemInteractive g1 = new MenuItemInteractive(RC_Items.createItem(
                Material.GOLD_INGOT, ""),
                RC_Items.getGlassPane(DyeColor.YELLOW),
                1, 9);

        final MenuItemInteractive s10 = new MenuItemInteractive(RC_Items.createItem(
                Material.IRON_INGOT, ""),
                RC_Items.getGlassPane(DyeColor.WHITE),
                1, 9);
        final MenuItemInteractive s1 = new MenuItemInteractive(RC_Items.createItem(
                Material.IRON_INGOT, ""),
                RC_Items.getGlassPane(DyeColor.WHITE),
                1, 9);

        final MenuItemInteractive k10 = new MenuItemInteractive(RC_Items.createItem(
                Material.NETHER_BRICK_ITEM, ""),
                RC_Items.getGlassPane(DyeColor.BROWN),
                1, 9);
        final MenuItemInteractive k1 = new MenuItemInteractive(RC_Items.createItem(
                Material.NETHER_BRICK_ITEM, ""),
                RC_Items.getGlassPane(DyeColor.BROWN),
                1, 9);

        final MenuItemInteractive[] item_values = new MenuItemInteractive[]{k1, k10, s1, s10, g1, g10, g100};
        // set value
        for(int i = 0; i < item_values.length; i++) {
            item_values[i].setAmount(0);
        }
        for (int i = values.length - 1; i >= 0; i--) {
            item_values[i].setAmount(values[i]);
        }

        // +++ ++ ++

        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                g100.increase();
            }
        }.setItem(MenuItemAPI.getItemPlus("+100 Gold")));
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                g10.increase();
            }
        }.setItem(MenuItemAPI.getItemPlus("+10 Gold")));
        menu.addMenuItem((new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                g1.increase();
            }
        }).setItem(MenuItemAPI.getItemPlus("+1 Gold")));
        menu.empty();
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                s10.increase();
            }
        }.setItem(MenuItemAPI.getItemPlus("+10 Silber")));
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                s1.increase();
            }
        }.setItem(MenuItemAPI.getItemPlus("+1 Silber")));
        menu.empty();
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                k10.increase();
            }
        }.setItem(MenuItemAPI.getItemPlus("+10 Kupfer")));
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                k1.increase();
            }
        }.setItem(MenuItemAPI.getItemPlus("+1 Kupfer")));

        // GGG SSS KK
        menu.addMenuItem(g100);
        menu.addMenuItem(g10);
        menu.addMenuItem(g1);
        menu.empty();
        menu.addMenuItem(s10);
        menu.addMenuItem(s1);
        menu.empty();
        menu.addMenuItem(k10);
        menu.addMenuItem(k1);

        // --- -- --
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                g100.decrease();
            }
        }.setItem(MenuItemAPI.getItemPlus("-100 Gold")));
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                g10.decrease();
            }
        }.setItem(MenuItemAPI.getItemPlus("-10 Gold")));
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                g1.decrease();
            }
        }.setItem(MenuItemAPI.getItemPlus("-1 Gold")));
        menu.empty();
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                s10.decrease();
            }
        }.setItem(MenuItemAPI.getItemPlus("-10 Silber")));
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                s1.decrease();
            }
        }.setItem(MenuItemAPI.getItemPlus("-1 Silber")));
        menu.empty();
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                k10.decrease();
            }
        }.setItem(MenuItemAPI.getItemPlus("-10 Kupfer")));
        menu.addMenuItem(new MenuItemAPI() {
            @Override
            public void trigger(Player player) {

                k1.decrease();
            }
        }.setItem(MenuItemAPI.getItemPlus("-1 Kupfer")));

        menu.activeOkCancelButton();
        ChestUI.getInstance().openMenu(player, menu, new MenuListener() {
            @Override
            public void cancel() {

                if (listener == null) {
                    return;
                }
                listener.cancel(player);
            }

            @Override
            public void accept() {

                if (listener == null) {
                    return;
                }
                double money = 0;
                int multiply = 1000000;
                for (int i = 0; i < item_values.length; i++) {
                    money += item_values[i].getAmount() * multiply;
                    multiply /= 10;
                }
                money = money / 100;
                listener.accept(player, money);
            }
        });
    }
}

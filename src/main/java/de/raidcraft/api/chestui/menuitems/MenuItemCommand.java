package de.raidcraft.api.chestui.menuitems;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * A item that execute a simple command
 *
 * @author Dragonfire
 */
public class MenuItemCommand extends MenuItemAPI {

    @Getter
    @Setter
    private String command;

    @Override
    public void trigger(Player player) {

        Bukkit.getServer().dispatchCommand(player, command);
    }
}

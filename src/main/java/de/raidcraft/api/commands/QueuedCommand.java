package de.raidcraft.api.commands;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.player.RCPlayer;
import org.bukkit.ChatColor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
* @author Silthus
*/
public class QueuedCommand {

    private final RCPlayer player;
    private final Object object;
    private final Method method;
    private final Object[] args;

    public QueuedCommand(final RCPlayer player, Object object, Method method, Object... args) {

        this.player = player;
        this.object = object;
        this.method = method;
        this.args = args;
        RaidCraft.getComponent(RaidCraftPlugin.class).queueCommand(this);
    }

    public RCPlayer getPlayer() {

        return player;
    }

    public void run() {

        try {
            method.setAccessible(true);
            method.invoke(object, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            player.sendMessage(ChatColor.RED + e.getMessage());
            e.printStackTrace();
        }
    }
}

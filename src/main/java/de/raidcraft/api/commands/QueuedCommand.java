package de.raidcraft.api.commands;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
* @author Silthus
*/
public class QueuedCommand {

    private final CommandSender sender;
    private final Object object;
    private final Method method;
    private final Object[] args;

    public QueuedCommand(final CommandSender sender, Object object, Method method, Object... args) {

        this.sender = sender;
        this.object = object;
        this.method = method;
        this.args = args;
        RaidCraft.getComponent(RaidCraftPlugin.class).queueCommand(this);
    }

    public CommandSender getSender() {

        return sender;
    }

    public void run() {

        try {
            method.setAccessible(true);
            method.invoke(object, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            e.printStackTrace();
        }
    }
}

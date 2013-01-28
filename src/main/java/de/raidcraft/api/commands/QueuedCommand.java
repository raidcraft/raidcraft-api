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

    public QueuedCommand(final CommandSender sender, Object object, String method, Object... args) throws NoSuchMethodException {

        this.sender = sender;
        this.object = object;
        Class[] argsClasses = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argsClasses[i] = args[i].getClass();
        }
        this.method = object.getClass().getDeclaredMethod(method, argsClasses);
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

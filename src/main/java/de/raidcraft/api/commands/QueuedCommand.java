package de.raidcraft.api.commands;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import org.apache.commons.lang.ClassUtils;
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
    private final Object[] args;
    private Method method;

    public QueuedCommand(final CommandSender sender, Object object, String methodName, Object... args) throws NoSuchMethodException {

        this.sender = sender;
        this.object = object;
        Class[] argsClasses = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argsClasses[i] = args[i].getClass();
        }
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (method.getParameterTypes().length == argsClasses.length) {
                boolean match = true;
                for (int i = 0; i < argsClasses.length; i++) {
                    if (method.getParameterTypes()[i].isPrimitive()) {
                        Class<?> aClass = ClassUtils.primitiveToWrapper(method.getParameterTypes()[i]);
                        if (aClass.isAssignableFrom(argsClasses[i])) {
                            continue;
                        } else {
                            match = false;
                            break;
                        }
                    }
                    if (!method.getParameterTypes()[i].isAssignableFrom(argsClasses[i])) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    this.method = method;
                    break;
                }
            }
        }
        if (this.method == null) throw new NoSuchMethodException(
                "No method signature found for " + methodName + " in " + object.getClass().getCanonicalName());
        this.args = args;
        RaidCraft.getComponent(RaidCraftPlugin.class).queueCommand(this);
        sender.sendMessage(ChatColor.RED + "Bitte bestätige den Befehl mit: " + ChatColor.GREEN + "/rcconfirm");
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

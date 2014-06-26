package de.raidcraft.api.config.builder;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author mdoering
 */
public interface ConfigGenerator {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Information {

        String value();

        String usage() default "";

        String desc();

        int min() default 0;

        int max() default -1;

        String flags() default "";

        String help() default "";

        boolean anyFlags() default false;

        boolean multiSection() default false;
    }

    public default Information getInformation(String name) {

        return ConfigBuilder.getConfigGeneratorInformation(name);
    }

    public default void printHelp(CommandSender sender, String name) {

        Information information = getInformation(name);
        sender.sendMessage(ChatColor.YELLOW + information.value() + " - " + ChatColor.GRAY + ChatColor.ITALIC + information.desc());
        sender.sendMessage(ChatColor.AQUA + "Usage: " + information.usage());
        sender.sendMessage(ChatColor.GRAY + "Help: " + ChatColor.ITALIC + information.help());
    }

    public default <T extends BasePlugin> void build(ConfigBuilder<T> builder, CommandContext args, Player player, String name) throws ConfigBuilderException {

        try {
            Method method = ConfigBuilder.getConfigGeneratorMethod(this, name);
            if (method == null) return;
            method.invoke(this, builder, args, player);
        } catch (IllegalAccessException | InvocationTargetException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }
}
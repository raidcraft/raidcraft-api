package de.raidcraft.api.config.builder;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.api.BasePlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * @author mdoering
 */
@FunctionalInterface
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

    public default Information getInformation() {

        try {
            Method method = getClass().getMethod("createSection", CommandContext.class, Player.class);
            return method.getAnnotation(Information.class);
        } catch (NoSuchMethodException ignored) {
        }
        return null;
    }

    public default void printHelp(CommandSender sender) {

        Information information = getInformation();
        sender.sendMessage(ChatColor.YELLOW + information.value() + " - " + ChatColor.GRAY + ChatColor.ITALIC + information.desc());
        sender.sendMessage(ChatColor.AQUA + "Usage: " + information.usage());
        sender.sendMessage(ChatColor.GRAY + "Help: " + ChatColor.ITALIC + information.help());
    }

    public <T extends BasePlugin> void build(ConfigBuilder<T> builder, CommandContext args, Player player) throws ConfigBuilderException;
}
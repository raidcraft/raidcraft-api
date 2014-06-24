package de.raidcraft.api.config.builder;

import com.sk89q.minecraft.util.commands.CommandContext;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mdoering
 */
@FunctionalInterface
public interface SectionBuilder {

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
    }

    public ConfigurationSection createSection(CommandContext args, Player player) throws ConfigBuilderException;
}
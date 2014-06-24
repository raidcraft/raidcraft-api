package de.raidcraft.api.action;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.api.config.builder.ConfigBuilderException;
import de.raidcraft.api.config.builder.SectionBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public interface ActionAPISectionBuilder extends SectionBuilder {

    @Override
    default ConfigurationSection createSection(CommandContext args, Player player) throws ConfigBuilderException {

        MemoryConfiguration config = new MemoryConfiguration();
        config.set("type", ActionAPI.getIdentifier(this));
        config.set("args", createArgsSection(args, player));
        return config;
    }

    ConfigurationSection createArgsSection(CommandContext args, Player player) throws ConfigBuilderException;
}

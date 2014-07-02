package de.raidcraft.api.config.builder;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.RaidCraftPlugin;
import org.bukkit.command.CommandSender;

/**
 * @author Silthus
 */
public class BaseBuilderCommand {

    private final RaidCraftPlugin plugin;

    public BaseBuilderCommand(RaidCraftPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"rccb", "builder"},
            desc = "Builder"
    )
    @NestedCommand(BuilderCommands.class)
    public void base(CommandContext args, CommandSender sender) {

    }
}

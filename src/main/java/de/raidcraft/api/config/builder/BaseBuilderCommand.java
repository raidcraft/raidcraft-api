package de.raidcraft.api.config.builder;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.RaidCraftBasePlugin;
import org.bukkit.command.CommandSender;

/**
 * @author Silthus
 */
public class BaseBuilderCommand {

    private final RaidCraftBasePlugin plugin;

    public BaseBuilderCommand(RaidCraftBasePlugin plugin) {

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

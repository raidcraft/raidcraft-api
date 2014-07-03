package de.raidcraft.api.config.builder;

import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.config.ConfigurationBase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public class BuilderCommands {

    private final RaidCraftPlugin plugin;

    public BuilderCommands(RaidCraftPlugin plugin) {

        this.plugin = plugin;
    }

    @com.sk89q.minecraft.util.commands.Command(
            aliases = {"add"},
            desc = "Adds another config step to the builder",
            min = 1,
            usage = "<name>",
            help = "See /actionapi for valid names",
            anyFlags = true
    )
    @CommandPermissions("raidcraft.configbuilder")
    public void add(CommandContext args, CommandSender sender) throws CommandException {

        ConfigBuilder<?> builder = ConfigBuilder.getBuilder((Player) sender);
        String name = args.getString(0);
        ConfigGenerator configGenerator = ConfigBuilder.getConfigGenerator(name);
        if (configGenerator == null) {
            throw new CommandException("No valid builder with the name " + name + " found!");
        }
        if (args.argsLength() > 1 && args.getString(1).equals("?")) {
            configGenerator.printHelp(sender, name);
            return;
        }
        ConfigBuilder.checkArguments(sender, args, configGenerator, name);
        configGenerator.build(builder,
                new CommandContext(args.argsLength() > 1 ? args.getSlice(1) : new String[0], args.getFlags()),
                (Player) sender,
                name);
    }

    @com.sk89q.minecraft.util.commands.Command(
            aliases = {"create"},
            desc = "Creates a new named config file in the builder base path.",
            anyFlags = true,
            min = 1
    )
    @CommandPermissions("raidcraft.configbuilder")
    public void create(CommandContext args, CommandSender sender) {

        ConfigBuilder builder = ConfigBuilder.getBuilder((Player) sender);
        ConfigurationBase oldConfig = builder.createConfig(args.getString(0));
        if (oldConfig != null) {
            sender.sendMessage(ChatColor.GREEN + "Finished building config: " + oldConfig.getName());
        }
        sender.sendMessage(ChatColor.GREEN + "Started new config: " + builder.getCurrentConfig().getName());
    }

    @com.sk89q.minecraft.util.commands.Command(
            aliases = {"save"},
            desc = "Saves all created configs to disk"
    )
    @CommandPermissions("raidcraft.configbuilder")
    public void save(CommandContext args, CommandSender sender) throws CommandException {

        if (!ConfigBuilder.isBuilder((Player) sender)) {
            throw new CommandException("You have no active config builder, cannot save nothing!");
        }
        ConfigBuilder builder = ConfigBuilder.getBuilder((Player) sender);
        builder.save();
        sender.sendMessage(ChatColor.GREEN + "Saved your built config files to disk! They are all located in: " + builder.getBasePath().getAbsolutePath());
    }
}

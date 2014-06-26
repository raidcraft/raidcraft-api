package de.raidcraft.api.config.builder;

import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.config.ConfigurationBase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mdoering
 */
public class BuilderCommands implements TabCompleter {

    private final RaidCraftPlugin plugin;

    public BuilderCommands(RaidCraftPlugin plugin) {

        this.plugin = plugin;
        plugin.getCommand("rccb").setTabCompleter(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String cmdLabel, String[] args) {

        if (args.length < 1 || !Arrays.asList("add").contains(args[0].toLowerCase())) {
            return null;
        }
        if (args.length == 2) {
            return ConfigBuilder.getConfigGenerators().keySet().stream()
                    .filter(cmd -> cmd.startsWith(args[1])).collect(Collectors.toList());
        }
        return null;
    }

    @com.sk89q.minecraft.util.commands.Command(
            aliases = {"add"},
            desc = "Adds another config step to the builder",
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
            anyFlags = true
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

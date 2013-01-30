package de.raidcraft.api.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.RaidCraftPlugin;
import org.bukkit.command.CommandSender;

/**
 * @author Silthus
 */
public class ConfirmCommand {

    private final RaidCraftPlugin plugin;

    public ConfirmCommand(RaidCraftPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"rcconfirm", "confirm"},
            desc = "Confirms the queued command.",
            min = 0
    )
    public void confirm(CommandContext args, CommandSender sender) throws CommandException {

        if (!plugin.getQueuedCommands().containsKey(sender.getName())) {
            throw new CommandException("Es gibt nichts was du aktuell bestätigen kannst!");
        }
        QueuedCommand command = plugin.getQueuedCommands().get(sender.getName());
        if (command instanceof QueuedCaptchaCommand) {
            if(args.argsLength() < 1) {
                throw new CommandException("Captcha vergessen! /rcconfirm <Captcha>");
            }
            if(!((QueuedCaptchaCommand) command).getCaptcha().equals(args.getString(0))) {
                throw new CommandException("Falscher Captcha Code! Bitte versuche es erneut.");
            }
        }
        command.run();
        plugin.getQueuedCommands().remove(sender.getName());
    }

}

package de.raidcraft.api.action;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.api.config.builder.ConfigBuilder;
import de.raidcraft.api.config.builder.ConfigGenerator;
import de.raidcraft.util.PastebinPoster;
import org.bukkit.command.CommandSender;

import java.util.Optional;

/**
 * @author Silthus
 */
public class ActionCommand {

    private final RaidCraftPlugin plugin;

    public ActionCommand(RaidCraftPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"actionapi"},
            desc = "Creates a HTML page with all triggers, requirements and actions"
    )
    @CommandPermissions("raidcraft.actionapi.createhtml")
    public void createHTML(CommandContext args, CommandSender sender) throws CommandException {

        StringBuilder sb = new StringBuilder();
        sb.append("########### ACTIONS ###########\n");
        ActionAPI.getActions().entrySet().forEach(
                entry -> {
                    Action<?> action = entry.getValue();
                    sb.append(entry.getKey());
                    if (action != null) {
                        Optional<ConfigGenerator.Information> information = ConfigBuilder.getInformation(entry.getKey());
                        if (information.isPresent()) {
                            sb.append(": ").append(information.get().desc());
                            for (String conf : information.get().conf()) {
                                sb.append("\n\t-").append(conf);
                            }
                        }
                    }
                    sb.append("\n");
                }
        );
        sb.append("\n\n########### REQUIREMENTS ###########\n");
        ActionAPI.getRequirements().entrySet().forEach(
                entry -> {
                    Requirement<?> requirement = entry.getValue();
                    sb.append(entry.getKey());
                    if (requirement != null) {
                        Optional<ConfigGenerator.Information> information = ConfigBuilder.getInformation(entry.getKey());
                        if (information.isPresent()) {
                            sb.append(": ").append(information.get().desc());
                            for (String conf : information.get().conf()) {
                                sb.append("\n\t-").append(conf);
                            }
                        }
                    }
                    sb.append("\n");
                }
        );
        sb.append("\n\n########### TRIGGER ###########\n");
        ActionAPI.getTrigger().entrySet().forEach(
                entry -> {
                    Trigger trigger = entry.getValue();
                    sb.append(entry.getKey());
                    if (trigger != null) {
                        Optional<ConfigGenerator.Information> information = ConfigBuilder.getInformation(entry.getKey());
                        if (information.isPresent()) {
                            sb.append(": ").append(information.get().desc());
                            for (String conf : information.get().conf()) {
                                sb.append("\n\t-").append(conf);
                            }
                        }
                    }
                    sb.append("\n");
                }
        );
        sender.sendMessage("Posting the list of triggers, actions and requirements to pastebin.org ...");
        PastebinPoster.paste(sb.toString(), new PastebinPoster.PasteCallback() {
            @Override
            public void handleSuccess(String url) {

                sender.sendMessage("Pasted your list at: " + url);
            }

            @Override
            public void handleError(String err) {

                sender.sendMessage("Failed to paste the list!");
            }
        });
    }
}

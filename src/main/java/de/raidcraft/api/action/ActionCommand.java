package de.raidcraft.api.action;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.action.action.ActionFactory;
import de.raidcraft.api.action.requirement.RequirementFactory;
import de.raidcraft.api.action.trigger.TriggerManager;
import de.raidcraft.util.PastebinPoster;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

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
    public void createHTML(CommandContext args, CommandSender sender) {

        StringBuilder sb = new StringBuilder();
        sb.append("########### ACTIONS ###########");
        ActionFactory.getInstance().getActions().keySet().forEach(
                key -> {
                    sb.append(key);
                    sb.append("\n");
                }
        );
        sb.append("\n\n########### REQUIREMENTS ###########");
        RequirementFactory.getInstance().getRequirements().keySet().forEach(
                key -> {
                    sb.append(key);
                    sb.append("\n");
                }
        );
        sb.append("\n\n########### TRIGGER ###########");
        TriggerManager.getInstance().getTrigger().entrySet().forEach(
                entry -> {
                    Arrays.asList(entry.getValue().getActions()).forEach(
                            action -> sb.append(entry.getKey()).append(".").append(action).append("\n")
                    );
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

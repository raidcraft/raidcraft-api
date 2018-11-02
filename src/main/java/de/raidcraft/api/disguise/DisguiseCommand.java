/** **************************************************************
 *                      Tales of Faldoria                       *
 *                                                              *
 *  This plugin was written for Tales of Faldoria and is not    *
 *  for public use                                              *
 *                                                              *
 * Website: https://www.faldoria.de                             *
 * Contact: info@faldoria.de                                    *
 *                                                              *
 *************************************************************** */
package de.raidcraft.api.disguise;

import com.google.common.base.Strings;
import com.sk89q.minecraft.util.commands.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.items.Skull;
import de.raidcraft.util.PaginatedResult;
import de.raidcraft.util.StringUtils;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.book.BookUtil;

/**
 *
 * @author xanily
 */
public class DisguiseCommand {

    public DisguiseCommand(RaidCraftPlugin plugin) {}

    @Command(
            aliases = {"skin"},
            desc = "Allows saving of skins."
    )
    @NestedCommand(SubCommands.class)
    @CommandPermissions("faldoria.disguise")
    public void disguiseCommand(CommandContext args, CommandSender sender) {

    }

    @Data
    public static class SubCommands {

        private final RaidCraftPlugin plugin;
        private DisguiseManager disguiseManager;

        public SubCommands(RaidCraftPlugin plugin) {
            this.plugin = plugin;
        }

        private DisguiseManager getDisguiseManager() {
            if (this.disguiseManager == null) {
                this.disguiseManager = RaidCraft.getComponent(DisguiseManager.class);
            }
            return this.disguiseManager;
        }

        @Command(
                aliases = {"save"},
                desc = "Saves the current player skin into the database.",
                min = 1,
                max = 3,
                flags = "d:",
                usage = "[-d <Description>] [Alias] [<Texture> <Signature>]",
                help = "Specify an optional alias and texture signature to save the skin under. You can specify a description for the disguise with the -d flag."
        )
        @CommandPermissions("faldoria.disguise.save")
        public void disguise(CommandContext args, CommandSender sender) throws CommandException {

            if (!(sender instanceof Player)) throw new CommandException("Command can only be executed as a player.");

            Player player = (Player) sender;

            String disguiseName = args.getString(0, null);

            if (getDisguiseManager().isAlreadyTaken(disguiseName)) {
                throw new CommandException("Es gibt bereits einen Skin mit dem Namen " + disguiseName);
            }

            Optional<Disguise> disguise = Optional.empty();

            if (args.argsLength() < 2) {
                disguise = getDisguiseManager().createDisguise(player, disguiseName, args.getFlag('d', null));
            } else if (args.argsLength() > 2) {
                if (!Skull.isBase64(args.getString(1)) || !Skull.isBase64(args.getString(2))) {
                    throw new CommandException("Skin texture and signature must be base64 encoded.");
                }
                disguise = Optional.of(getDisguiseManager().createDisguise(disguiseName, args.getString(1), args.getString(2), args.getFlag('d', null)));
            }

            if (disguise.isPresent()) {
                sender.sendMessage(ChatColor.GREEN + "Disguise wurde erfolgreich unter dem Namen " + ChatColor.AQUA + disguise.get().getAlias() + ChatColor.GREEN + " gespeichert.");
            } else {
                sender.sendMessage(ChatColor.RED + "Die Disguise konnte leider nicht gespeichert werden.");
            }
        }

        @Command(
                aliases = "list",
                desc = "Lists all existing disguises.",
                flags = "p:",
                help = "[-p <page>]"
        )
        @CommandPermissions("faldoria.disguise.list")
        public void list(CommandContext args, CommandSender sender) throws CommandException {

            new PaginatedResult<Disguise>("Disguises") {
                @Override
                public String format(Disguise entry) {
                    String name = ChatColor.YELLOW + "[" + ChatColor.AQUA + entry.getAlias() + " " + ChatColor.GRAY + "(ID:" + entry.getId() + ")" + ChatColor.YELLOW + "]";
                    if (!Strings.isNullOrEmpty(entry.getDescription())) {
                        name += ChatColor.BLUE + ": " + ChatColor.GRAY + entry.getDescription();
                    }
                    return name;
                }
            }.display(sender, getDisguiseManager().getAllDisguises(), args.getFlagInteger('p', 1));
        }

        @Command(
                aliases = {"delete", "remove"},
                desc = "Deletes the disguise with the given alias from the database",
                min = 1,
                usage = "<Alias>"
        )
        @CommandPermissions("faldoria.disguise.delete")
        public void delete(CommandContext args, CommandSender sender) {

            getDisguiseManager().getDisguise(args.getString(0)).ifPresent(disguise -> {
                disguise.delete();
                sender.sendMessage(ChatColor.GREEN + "Deleted " + ChatColor.AQUA + disguise.getAlias() + ChatColor.GREEN + " successfully.");
            });
        }
    }
}

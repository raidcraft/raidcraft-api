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

import com.sk89q.minecraft.util.commands.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.items.Skull;
import de.raidcraft.util.StringUtils;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author xanily
 */
public class DisguiseCommand {

    public DisguiseCommand(RaidCraftPlugin plugin) {}

    @Command(
            aliases = {"cdisguise", "cdisg", "cd", "skin"},
            desc = "Allows saving of skins."
    )
    @NestedCommand(SubCommands.class)
    @CommandPermissions("faldoria.disguise")
    public void disguiseCommand(CommandContext args, CommandSender sender) {

    }

    @Data
    public class SubCommands {

        private final RaidCraftPlugin plugin;
        private final DisguiseManager disguiseManager;

        public SubCommands(RaidCraftPlugin plugin) {
            this.plugin = plugin;
            this.disguiseManager = RaidCraft.getComponent(DisguiseManager.class);
        }

        @Command(
                aliases = {"save"},
                desc = "Saves the current player skin into the database.",
                max = 1,
                usage = "[Alias]",
                help = "Specify an optional alias to save the skin under."
        )
        @CommandPermissions("faldoria.disguise.save")
        public void disguise(CommandContext args, CommandSender sender) throws CommandException {

            if (!(sender instanceof Player)) throw new CommandException("Command can only be executed as a player.");

            Player player = (Player) sender;

            String disguiseName = args.getString(0);

            if (getDisguiseManager().isAlreadyTaken(disguiseName)) {
                throw new CommandException("Es gibt bereits einen Skin mit dem Namen " + disguiseName);
            }

            Optional<Disguise> disguise = getDisguiseManager().createDisguise(player, disguiseName);

            if (disguise.isPresent()) {
                sender.sendMessage(ChatColor.GREEN + "Disguise wurde erfolgreich unter dem Namen " + ChatColor.AQUA + disguise.get().getAlias() + ChatColor.GREEN + " gespeichert.");
            } else {
                sender.sendMessage(ChatColor.RED + "Die Disguise konnte leider nicht gespeichert werden.");
            }
        }

        @Command(
                aliases = {"signature"},
                desc = "Saves the encoded skin texture into the database.",
                max = 3,
                usage = "<Alias> <Texture> <Signature>",
                help = "Provide the encoded texture and signature strings to save a skin directly into the database."
        )
        @CommandPermissions("faldoria.disguise.save")
        public void saveUrl(CommandContext args, CommandSender sender) throws CommandException {

            String alias = args.getString(0);

            if (getDisguiseManager().isAlreadyTaken(alias)) {
                throw new CommandException("Es gibt bereits einen Skin mit dem Namen " + alias);
            }

            if (!Skull.isBase64(args.getString(1)) || !Skull.isBase64(args.getString(2))) {
                throw new CommandException("Texture oder Signature ist kein Base64 String.");
            }

            Disguise disguise = getDisguiseManager().createDisguise(alias, args.getString(1), args.getString(2));

            sender.sendMessage(ChatColor.GREEN + "Disguise wurde erfolgreich unter dem Namen " + ChatColor.AQUA + disguise.getAlias() + ChatColor.GREEN + " gespeichert.");
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

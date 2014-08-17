package de.raidcraft.util;

import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Dragonfire
 */
public class CommandUtil {

    /**
     * Warp the player or defined player in playerStartIndex to a location
     *
     * @param args             command args
     * @param sender           command sender
     * @param to               sender to warp
     * @param playerStartIndex start argument for player name
     *
     * @return the target (sender or player defined)
     *
     * @throws CommandException
     */
    public static Player warp(CommandContext args, CommandSender sender, Location to, int playerStartIndex) throws CommandException {

        if (args.argsLength() <= playerStartIndex) {
            if (!(sender instanceof Player)) {
                throw new CommandException("Specifiy a player to warp");
            }
            ((Player) sender).teleport(to);
            return (Player) sender;
        }
        // warp another player
        UUID uuid = UUIDUtil.convertPlayer(args.getString(playerStartIndex));
        if (uuid == null) {
            throw new CommandException("Player not exists");
        }
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            throw new CommandException("Player is not online");
        }
        player.teleport(to);
        return player;
    }
}

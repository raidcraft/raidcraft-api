package de.raidcraft.api.tags;

import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.util.FancyPaginatedResult;
import de.raidcraft.util.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TagCommands {

    public TagCommands(RaidCraftPlugin plugin) {

    }

    @Command(
            aliases = {"tags", "tag", "rctag", "rctags"},
            desc = "Ermöglicht es Quest Tags von Spielern einzusehen und zu verändern."
    )
    @CommandPermissions("tags.admin")
    @NestedCommand(NestedTagCommands.class)
    public void tags(CommandContext args, CommandSender sender) throws CommandException {

    }

    public static class NestedTagCommands {

        public NestedTagCommands(RaidCraftPlugin plugin) {

        }

        @Command(
                aliases = {"list"},
                desc = "Zeit alle Tags des Spielers an.",
                help = "-f: filtert die Tags nach dem Stichwort, z.B. /tags list -f ankanor",
                usage = "[-p <page>] [-f <filter>] [Player]",
                flags = "p:f:"
        )
        public void list(CommandContext args, CommandSender sender) throws CommandException {

            String playerName = args.getString(0, sender.getName());
            Player player = Bukkit.getPlayer(playerName);
            if (player == null) throw new CommandException("Unknown player " + playerName);

            List<TPlayerTag> tags = TPlayerTag.findTags(player.getUniqueId());

            if (args.hasFlag('f')) {
                tags = tags.stream()
                        .filter(tag -> tag.getTag().getId().contains(args.getFlag('f')))
                        .collect(Collectors.toList());
            }

            tags.sort(Comparator.comparing(o -> o.getTag().getId()));

            new FancyPaginatedResult<TPlayerTag>("Player Tags") {

                @Override
                public FancyMessage format(TPlayerTag entry) {
                    return new FancyMessage(entry.getTag().getId()).color(ChatColor.GOLD).suggest("/tag delete -i " + entry.getPlayerId() + " " + entry.getTag().getId())
                            .text(" (").color(ChatColor.DARK_GRAY)
                            .text(entry.getCount() + "").color(ChatColor.AQUA)
                            .text(")").color(ChatColor.DARK_GRAY)
                            .text(": ").color(ChatColor.GOLD)
                            .text(entry.getTag().getDescription()).color(ChatColor.GRAY);
                }
            }.display(sender, tags, args.getFlagInteger('p', 1));
        }

        @Command(
                aliases = {"delete", "remove"},
                desc = "Löscht den Tag des angegebenen Spielers.",
                usage = "[-p <Spieler> | -i <UUID>] <tag>",
                min = 1,
                flags = "p:i:",
                help = "-i: UUID des Spielers"
        )
        public void delete(CommandContext args, CommandSender sender) throws CommandException {

            OfflinePlayer player = getPlayer(args, sender);

            String tagName = args.getJoinedStrings(0);
            Optional<TPlayerTag> tag = TPlayerTag.findTag(player.getUniqueId(), tagName);
            if (!tag.isPresent()) {
                throw new CommandException("Tag " + tagName + " des Spielers " + player.getName() + " nicht gefunden!");
            }

            tag.get().delete();
            sender.sendMessage(ChatColor.GREEN + "Tag " + ChatColor.GOLD + tagName
                    + ChatColor.DARK_GRAY + "(" + ChatColor.AQUA + tag.get().getCount() + ChatColor.DARK_GRAY + ")" +
                    ChatColor.GREEN + " wurde erfolgreich für " + ChatColor.RED + player.getName() + ChatColor.DARK_RED + " gelöscht.");
        }

        @Command(
                aliases = {"add", "set"},
                desc = "Setzt oder erhöht den Tag des Spielers.",
                usage = "[-p <Spieler> | -i <UUID>] [-c <count>] <tag>",
                min = 1,
                help = "-c: counter des tags"
        )
        public void add(CommandContext args, CommandSender sender) throws CommandException {

            OfflinePlayer player = getPlayer(args, sender);

            String tagName = args.getJoinedStrings(0);
            if (!TTag.findTag(tagName).isPresent()) {
                throw new CommandException("Es gibt keinen Tag mit dem Namen " + tagName);
            }

            TPlayerTag tag = TPlayerTag.createTag(player, tagName, args.getFlagInteger('c', 1));

            sender.sendMessage(ChatColor.GREEN + "Tag " + ChatColor.GOLD + tagName
                    + ChatColor.DARK_GRAY + "(" + ChatColor.AQUA + tag.getCount() + ChatColor.DARK_GRAY + ")" +
                    ChatColor.GREEN + " wurde erfolgreich für " + ChatColor.RED + player.getName() + ChatColor.DARK_GREEN + " gesetzt.");
        }

        private OfflinePlayer getPlayer(CommandContext args, CommandSender sender) throws CommandException {
            String playerName = args.getString(0, sender.getName());
            Player player = Bukkit.getPlayer(playerName);
            if (player == null && !args.hasFlag('i')) throw new CommandException("Unknown player " + playerName);
            UUID id = player != null ? player.getUniqueId() : UUID.fromString(args.getFlag('i'));
            return Bukkit.getOfflinePlayer(id);
        }
    }
}

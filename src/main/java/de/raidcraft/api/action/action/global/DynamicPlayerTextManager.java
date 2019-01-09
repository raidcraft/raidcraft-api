package de.raidcraft.api.action.action.global;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.action.ActionConfigWrapper;
import de.raidcraft.util.StringUtils;
import de.raidcraft.util.TimeUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.upperlevel.spigot.book.BookUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class DynamicPlayerTextManager implements Component {

    private final RaidCraftPlugin plugin;
    private static final Map<String, BukkitTask> actionTasks = new HashMap<>();

    public DynamicPlayerTextManager(RaidCraftPlugin plugin) {

        this.plugin = plugin;
    }

    public void registerPlayerAction(Player player, ActionConfigWrapper<Player> action, ConfigurationSection config) {

        String text = config.getString("text");
        text = RaidCraft.replaceVariables(player, text);
        String uuid = UUID.randomUUID().toString();

        BukkitTask task = new DynamicTextTask(uuid, player, action).runTaskLater(getPlugin(), TimeUtil.secondsToTicks(StringUtils.calculateAverageReadingTime(text)));
        actionTasks.put(uuid, task);

        player.sendMessage(BookUtil.TextBuilder.of("[").color(ChatColor.DARK_GRAY).text("Antworte").color(ChatColor.GRAY).text("]: ").color(ChatColor.DARK_GRAY)
                .text(text).color(ChatColor.GOLD)
                .onClick(BookUtil.ClickAction.runCommand("rcdynamictextaction " + uuid))
                .build());
    }

    @Command(
            aliases = {"rcdynamictextaction"},
            desc = "Executes the action with the given UUID.",
            min = 1
    )
    public void confirm(CommandContext args, CommandSender sender) throws CommandException {

        String uuid = args.getJoinedStrings(0);
        BukkitTask task = actionTasks.remove(uuid);
        if (task instanceof Runnable) {
            task.cancel();
            ((Runnable) task).run();
        } else {
            throw new CommandException("Du hast bereits geantwortet.");
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public class DynamicTextTask extends BukkitRunnable {

        private final String uuid;
        private final Player player;
        private final ActionConfigWrapper<Player> context;

        @Override
        public void run() {

            actionTasks.remove(uuid);
            getContext().executeChildActions(getPlayer());
        }
    }
}

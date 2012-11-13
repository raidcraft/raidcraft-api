package de.raidcraft.api;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.database.Table;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.api.player.UnknownPlayerException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Silthus
 */
public abstract class BasePlugin extends JavaPlugin implements CommandExecutor {

    private Database database;
    private CommandsManager<CommandSender> commands;
    private CommandsManagerRegistration commandRegistration;

    public final void onEnable() {

        this.database = new Database(this);

        this.commands = new CommandsManager<CommandSender>() {

            @Override
            public boolean hasPermission(CommandSender sender, String s) {

                return sender.hasPermission(s);
            }
        };
        commandRegistration = new CommandsManagerRegistration(this, this, commands);
    }

    public final void onDisable() {


    }

    public abstract void enable();

    public abstract void disable();

    @Override
    public final boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        try {
            commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "Du hast nicht genügend Rechte für diesen Befehl.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(ChatColor.RED + "Zahl als Argument erwartet. Buchstabe/Wort erhalten.");
            } else {
                sender.sendMessage(ChatColor.RED + "An error has occurred. See console.");
                e.printStackTrace();
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }
        return true;
    }

    public final void registerTable(Class<? extends Table> clazz, Table table) {

        database.registerTable(clazz, table);
    }

    public final void registerCommands(Class<?> clazz) {

        commandRegistration.register(clazz);
    }

    public final void registerEvents(Listener listener) {

        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    public final RCPlayer getPlayer(Player player) {

        return RaidCraft.getPlayer(player);
    }

    public final RCPlayer getPlayer(String player) {

        return RaidCraft.getPlayer(player);
    }
}

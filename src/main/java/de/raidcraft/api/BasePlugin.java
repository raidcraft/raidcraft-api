package de.raidcraft.api;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.database.Table;
import de.raidcraft.api.player.RCPlayer;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Silthus
 */
public abstract class BasePlugin extends JavaPlugin implements CommandExecutor {

    private static Database database;
    private CommandsManager<CommandSender> commands;
    private CommandsManagerRegistration commandRegistration;
    // vault variables
    private static Economy economy;
    private static Chat chat;
    private static Permission permission;

    public BasePlugin() {

        if (database == null) {
            database = new Database(this);
        }
    }

    public final void onEnable() {

        Plugin plugin = Bukkit.getPluginManager().getPlugin("Vault");
        if (plugin != null) {
            if (economy == null) setupEconomy();
            if (chat == null) setupChat();
            if (permission == null) setupPermissions();
        }

        this.commands = new CommandsManager<CommandSender>() {

            @Override
            public boolean hasPermission(CommandSender sender, String s) {

                return sender.hasPermission(s);
            }
        };
        commandRegistration = new CommandsManagerRegistration(this, this, commands);
        // call the sub plugins to enable
        enable();
    }

    public final void onDisable() {

        this.commandRegistration.unregisterCommands();
        // call the sub plugin to disable
        disable();
    }

    public abstract void enable();

    public abstract void disable();

    public void reload() {

        disable();
        enable();
    }

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

    public final Economy getEconomy() {

        return economy;
    }

    public final Chat getChat() {

        return chat;
    }

    public final Permission getPermissions() {

        return permission;
    }

    private boolean setupPermissions() {

        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private boolean setupChat() {

        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    private boolean setupEconomy() {

        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
}

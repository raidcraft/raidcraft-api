package de.raidcraft.api;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.commands.QueuedCommand;
import de.raidcraft.api.config.Config;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.database.Table;
import de.raidcraft.api.language.ConfigTranslationProvider;
import de.raidcraft.api.language.TranslationProvider;
import de.raidcraft.api.player.RCPlayer;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public abstract class BasePlugin extends JavaPlugin implements CommandExecutor, Component, TranslationProvider {

    // vault variables
    private static Chat chat;
    private static Permission permission;
    // member variables
    private final Map<String, QueuedCommand> queuedCommands = new HashMap<>();
    private Database database;
    private TranslationProvider translationProvider;
    private CommandsManager<CommandSender> commands;
    private CommandsManagerRegistration commandRegistration;

    public final void onEnable() {

        // lets register the plugin as component
        RaidCraft.registerComponent(getClass(), this);

        // create default folders
        getDataFolder().mkdirs();
        // add translation provider
        this.translationProvider = new ConfigTranslationProvider(this);

        Plugin plugin = Bukkit.getPluginManager().getPlugin("Vault");
        if (plugin != null) {
            if (chat == null) {
                if (setupChat()) getLogger().info(plugin.getName() + "-v" + plugin.getDescription().getVersion() + ": loaded Chat API.");
                else getLogger().info(plugin.getName() + "-v" + plugin.getDescription().getVersion() + ": failed to load Chat API.");
            }
            if (permission == null) {
                if (setupPermissions())
                    getLogger().info(plugin.getName() + "-v" + plugin.getDescription().getVersion() + ": loaded Permissions API.");
                else getLogger().info(plugin.getName() + "-v" + plugin.getDescription().getVersion() + ": failed to load Permissions API.");
            }
        }

        this.commands = new CommandsManager<CommandSender>() {

            @Override
            public boolean hasPermission(CommandSender sender, String s) {

                return sender.isOp() || sender.hasPermission(s);
            }
        };

        this.commands.setInjector(new SimpleInjector(this));
        this.commandRegistration = new CommandsManagerRegistration(this, this, this.commands);
        // check if the database needs to be setup
        if (getDatabaseClasses().size() > 0) {
            loadDatabase();
        }
        // call the sub plugins to enable
        enable();

        PluginDescriptionFile description = getDescription();
        getLogger().info(description.getName() + "-v" + description.getVersion() + " enabled.");
    }

    public final void onDisable() {

        this.commandRegistration.unregisterCommands();
        // call the sub plugin to disable
        disable();
        PluginDescriptionFile description = getDescription();
        RaidCraft.unregisterComponent(getClass());
        getLogger().info(description.getName() + "-v" + description.getVersion() + " disabled.");
    }

    public abstract void enable();

    public abstract void disable();

    protected void loadDatabase() {

        // load the persistance database if used
        if (getDatabaseClasses().size() > 0) {
            try {
                for (Class<?> clazz : getDatabaseClasses()) {
                    getDatabase().find(clazz).findRowCount();
                }
            } catch (Throwable e) {
                // install the dll
                installDDL();
            }
        }
    }

    public void reload() {

        disable();
        enable();
    }

    public final <T extends Config> T configure(T config, boolean annotations) {

        config.load(annotations);
        return config;
    }

    public final <T extends Config> T configure(T config) {

        return configure(config, true);
    }

    public final void queueCommand(final QueuedCommand command) {

        queuedCommands.put(command.getSender().getName(), command);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {

                queuedCommands.remove(command.getSender().getName());
            }
        }, 600L);
        // 30 second remove delay
    }

    public final Map<String, QueuedCommand> getQueuedCommands() {

        return queuedCommands;
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

        if (database == null) {
            database = new Database();
        }
        database.registerTable(clazz, table);
    }

    public final void registerCommands(Class<?> clazz) {

        commandRegistration.register(clazz);
    }

    public final void registerEvents(Listener listener) {

        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    public RCPlayer getPlayer(Player player) {

        return RaidCraft.getPlayer(player);
    }

    public RCPlayer getPlayer(String player) {

        return RaidCraft.getPlayer(player);
    }

    public final Chat getChat() {

        return chat;
    }

    public final Permission getPermissions() {

        return permission;
    }

    public TranslationProvider getTranslationProvider() {

        return translationProvider;
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
}

package de.raidcraft.api;

import com.avaje.ebean.EbeanServer;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
import com.sk89q.minecraft.util.commands.SimpleInjector;
import com.sk89q.minecraft.util.commands.WrappedCommandException;
import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.commands.QueuedCommand;
import de.raidcraft.api.config.Config;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.database.Table;
import de.raidcraft.api.ebean.DatabaseConfig;
import de.raidcraft.api.ebean.RaidCraftDatabase;
import de.raidcraft.api.language.ConfigTranslationProvider;
import de.raidcraft.api.language.TranslationProvider;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.tables.RcLogLeevel;
import de.raidcraft.tables.TPlugin;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public abstract class BasePlugin extends JavaPlugin implements CommandExecutor, Component {

    // vault variables
    private static Chat chat;
    private static Permission permission;
    // member variables
    private final Map<String, QueuedCommand> queuedCommands = new HashMap<>();
    private Database database;
    private RaidCraftDatabase ebeanDatabase;
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
                if (setupChat()) {
                    getLogger().info(plugin.getName() + "-v" + plugin.getDescription().getVersion() + ": loaded Chat API.");
                } else {
                    getLogger().info(plugin.getName() + "-v" + plugin.getDescription().getVersion() + ": failed to load Chat API.");
                }
            }
            if (permission == null) {
                if (setupPermissions()) {
                    getLogger().info(plugin.getName() + "-v" + plugin.getDescription().getVersion() + ": loaded Permissions API.");
                } else {
                    getLogger().info(plugin.getName() + "-v" + plugin.getDescription().getVersion() + ": failed to load Permissions API.");
                }
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
            getDatabase();
        }
        // call the sub plugins to enable
        enable();

        // log plugin activation into db
        PluginDescriptionFile description = getDescription();
        getLogger().info(description.getName() + "-v" + description.getVersion() + " enabled.");
        TPlugin tPlugin = new TPlugin();
        tPlugin.setAuthor(String.join(", ", this.getDescription().getAuthors()));
        tPlugin.setName(this.getDescription().getFullName());
        tPlugin.setVersion(this.getDescription().getVersion());
        tPlugin.setLastActive(new Date());
        getDatabase().save(tPlugin);
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

    @Override
    public EbeanServer getDatabase() {

        if (this.ebeanDatabase == null) {
            this.ebeanDatabase = new RaidCraftDatabase(this);
            DatabaseConfig config = configure(new DatabaseConfig(this));
            config.save();
            this.ebeanDatabase.initializeDatabase(config);
        }
        return this.ebeanDatabase.getDatabase();
    }

    public void reload() {

        disable();
        enable();
    }

    @Deprecated
    /**
     * @deprecated Please use the configure(T config) method instead.
     * Annotations are now only loaded if there are any.
     */
    public final <T extends Config> T configure(T config, boolean annotations) {

        config.load();
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

        registerCommands(clazz, null);
    }

    public final void registerCommands(Class<?> clazz, String host) {

        if (host == null) {
            host = this.getName();
        }
        RaidCraft.getComponent(RaidCraftPlugin.class).trackCommand(clazz, host, null);
        commandRegistration.register(clazz);
    }

    public final void registerEvents(Listener listener) {

        RaidCraft.registerEvents(listener, this);
    }

    public RCPlayer getPlayer(Player player) {

        return RaidCraft.getPlayer(player);
    }

    // TODO: UUID rework
    @Deprecated
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

    // Rc log methods for informations

    public void info(String message) {

        info(message, null);
    }

    public void info(String message, String category) {

        log(message, category, RcLogLeevel.INFO);
    }

    public void log(String message, String category, RcLogLeevel level) {

        String tcategory = getName();
        if (category != null && !category.equals("")) {
            tcategory += "." + category;
        }
        RaidCraft.log(message, tcategory, level);
    }

}

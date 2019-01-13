package de.raidcraft.api;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.commands.QueuedCommand;
import de.raidcraft.api.components.*;
import de.raidcraft.api.components.loader.ClassLoaderComponentLoader;
import de.raidcraft.api.components.loader.ClassPathComponentLoader;
import de.raidcraft.api.components.loader.ConfigListedComponentLoader;
import de.raidcraft.api.components.loader.JarFilesComponentLoader;
import de.raidcraft.api.config.Config;
import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.api.ebean.DatabaseConfig;
import de.raidcraft.api.ebean.RaidCraftDatabase;
import de.raidcraft.api.language.ConfigTranslationProvider;
import de.raidcraft.api.language.TranslationProvider;
import de.raidcraft.api.player.RCPlayer;
import fr.zcraft.zlib.core.ZPlugin;
import io.ebean.EbeanServer;
import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;

import javax.persistence.OneToMany;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.logging.Level;

/**
 * @author Silthus
 */
@Getter
public abstract class BasePlugin extends ZPlugin implements CommandExecutor, Component {

    // vault variables
    @Getter
    private static Chat chat;
    @Getter
    private static Permission permission;

    // member variables
    private final Map<String, QueuedCommand> queuedCommands = new HashMap<>();
    private TranslationProvider translationProvider;
    private CommandsManager<CommandSender> commands;
    private CommandsManagerRegistration commandRegistration;
    private RaidCraftDatabase database;
    private ComponentManager<BukkitComponent> componentManager;

    public final void onEnable() {

        Class<OneToMany> klass = OneToMany.class;
        URL resource = klass.getResource('/' + klass.getName().replace('.', '/') + ".class");
        getLogger().info(resource.toString());

        super.onEnable();

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
            getRcDatabase();
        }
        // call the sub plugins to enable
        enable();

        setupComponentManager(getJarFile());

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, this::loadDependencyConfigs, 5 * 20L);
    }

    public final void onDisable() {

        super.onDisable();

        this.commandRegistration.unregisterCommands();
        this.getServer().getScheduler().cancelTasks(this);
        componentManager.unloadComponents();
        // call the sub plugin to disable
        disable();
        PluginDescriptionFile description = getDescription();
        RaidCraft.unregisterComponent(getClass());
        getLogger().info(description.getName() + "-v" + description.getVersion() + " disabled.");
    }

    public abstract void enable();

    /**
     * Override this method to load your plugins configs that depend on other plugins.
     */
    public void loadDependencyConfigs() {

    }

    public abstract void disable();

    public EbeanServer getRcDatabase() {

        if (database == null) {
            this.database = new RaidCraftDatabase(this);
            this.database.initializeDatabase(configure(new DatabaseConfig(this)));
        }

        return this.database.getDatabase();
    }

    /**
     * Provides a list of all classes that should be persisted in the database
     *
     * @return List of Classes that are Ebeans
     */
    public List<Class<?>> getDatabaseClasses() {
        return new ArrayList<Class<?>>();
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

        return configure(config);
    }

    public final <T extends Config> T configure(T config) {

        config.load();
        return config;
    }

    public final SimpleConfiguration<BasePlugin> configure(String configName) {
        return configure(new SimpleConfiguration<>(this, configName));
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

    @SuppressWarnings("unchecked")
    protected void setupComponentManager(JarFile jarFile) {
        componentManager = new ComponentManager<BukkitComponent>(
                getLogger(),
                BukkitComponent.class,
                configure(new SimpleConfiguration<BasePlugin>(this, "components.yml"))
        ) {
            @Override
            protected void setUpComponent(BukkitComponent component) {
                // Create a CommandsManager instance
                CommandsManager<CommandSender> commands = new CommandsManager<CommandSender>() {
                    @Override
                    public boolean hasPermission(CommandSender sender, String permission) {
                        return sender.isOp() || sender.hasPermission(permission);
                    }
                };
                commands.setInjector(new SimpleInjector(component));
                component.setUp(BasePlugin.this, commands);
            }
        };

        registerComponentLoaders(jarFile);

        try {
            componentManager.loadComponents(this);
        } catch (Throwable e) {
            getLogger().severe("Unable to load components of: " + getName());
            getLogger().severe(e.getMessage());
        }

        componentManager.enableComponents();
    }

    private void registerComponentLoaders(JarFile jarFile) {
        // -- Component loaders
        final File configDir = new File(getDataFolder(), "config/");

        FileConfiguration globalConfig = getConfig();
        SimpleConfiguration<BasePlugin> config = configure(new SimpleConfiguration<BasePlugin>(this, "components.yml"));

        componentManager.addComponentLoader(new ConfigListedComponentLoader(getLogger(),
                globalConfig,
                config,
                configDir));

        componentManager.addComponentLoader(new ClassPathComponentLoader(getLogger(), configDir, jarFile) {
            @Override
            public FileConfiguration createConfigurationNode(File configFile) {
                return BasePlugin.this.configure(new SimpleConfiguration<>(BasePlugin.this, configFile));
            }
        });

        for (String dir : config.getStringList("component-class-dirs")) {
            final File classesDir = new File(getDataFolder(), dir);
            if (!classesDir.exists() || !classesDir.isDirectory()) {
                classesDir.mkdirs();
            }
            componentManager.addComponentLoader(new ClassLoaderComponentLoader(getLogger(), classesDir, configDir) {
                @Override
                public YamlConfiguration createConfigurationNode(File file) {
                    return BasePlugin.this.configure(new SimpleConfiguration<>(BasePlugin.this, file));
                }
            });
        }

        for (String dir : config.getStringList("component-jar-dirs")) {
            final File classesDir = new File(getDataFolder(), dir);
            if (!classesDir.exists() || !classesDir.isDirectory()) {
                classesDir.mkdirs();
            }
            componentManager.addComponentLoader(new JarFilesComponentLoader(getLogger(), classesDir, configDir) {
                @Override
                public YamlConfiguration createConfigurationNode(File file) {
                    return BasePlugin.this.configure(new SimpleConfiguration<>(BasePlugin.this, file));
                }
            });
        }

        // -- Annotation handlers
        componentManager.registerAnnotationHandler(InjectComponent.class, new InjectComponentAnnotationHandler(componentManager));
    }

    protected boolean setupPermissions() {

        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private boolean setupChat() {

        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    public final void registerCommands(Class<?> clazz) {

        registerCommands(clazz, null);
    }

    public final void registerCommands(Class<?> clazz, String host) {

        if (host == null) {
            host = this.getName();
        }
        getPlugin(RaidCraftPlugin.class).trackCommand(clazz, host, null);
        commandRegistration.register(clazz);
    }

    public final void registerEvents(Listener listener) {

        RaidCraft.registerEvents(listener, this);
    }

    public final void unregisterEvents(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public RCPlayer getPlayer(Player player) {

        return RaidCraft.getPlayer(player);
    }

    // TODO: UUID rework
    @Deprecated
    public RCPlayer getPlayer(String player) {

        return RaidCraft.getPlayer(player);
    }

    public TranslationProvider getTranslationProvider() {

        return translationProvider;
    }

    /**
     * Checks permissions.
     *
     * @param sender The sender to check
     * @param perm The permission to check
     * @return Whether the sender has the permission
     */
    public boolean hasPermission(CommandSender sender, String perm) {
        if (!(sender instanceof Player)) {
            return (sender.isOp() || sender instanceof ConsoleCommandSender
                    || sender instanceof BlockCommandSender
                    || getPermission().has(sender, perm));
        }
        return hasPermission(sender, ((Player) sender).getWorld(), perm);
    }

    public boolean hasPermission(CommandSender sender, World world, String perm) {
        if (sender.isOp() || sender instanceof ConsoleCommandSender
                || sender instanceof BlockCommandSender) {
            return true;
        }

        // Invoke the permissions resolver
        if (sender instanceof Player) {
            Player player = (Player) sender;
            return getPermission().has(player, perm);
        }

        return false;
    }

    /**
     * Checks permissions and throws an exception if permission is not met.
     *
     * @param sender The sender to check
     * @param perm the permission to check
     * @throws com.sk89q.minecraft.util.commands.CommandPermissionsException if the sender
     * doesn't have the required permission
     */
    public void checkPermission(CommandSender sender, String perm)
            throws CommandPermissionsException {
        if (!hasPermission(sender, perm)) {
            throw new CommandPermissionsException();
        }
    }

    public void checkPermission(CommandSender sender, World world, String perm)
            throws CommandPermissionsException {
        if (!hasPermission(sender, world, perm)) {
            throw new CommandPermissionsException();
        }
    }

    // Rc log methods for informations

    public void severe(String message) {

        log(message, null, Level.SEVERE);
        getLogger().severe(message);
    }

    public void warning(String message) {

        log(message, null, Level.WARNING);
        getLogger().warning(message);
    }

    public void info(String message) {

        getLogger().info(message);
    }

    public void info(String message, String category) {

        getLogger().info(message);
    }

    public void log(String message, String category, Level level) {

        RaidCraft.LOGGER.log(level, message);
    }

}

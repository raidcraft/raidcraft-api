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
import de.raidcraft.RaidCraftBasePlugin;
import de.raidcraft.api.config.Config;
import de.raidcraft.api.ebean.DatabaseConfig;
import de.raidcraft.api.ebean.RaidCraftDatabase;
import de.raidcraft.api.language.ConfigTranslationProvider;
import de.raidcraft.api.language.TranslationProvider;
import de.raidcraft.tables.RcLogLeevel;
import de.raidcraft.tables.TPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;

/**
 * @author Silthus
 */
public abstract class BasePlugin extends JavaPlugin implements CommandExecutor, Component {

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
        logEnableStep();
    }

    private void logEnableStep() {
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


    public final <T extends Config> T configure(T config) {

        config.load();
        return config;
    }

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

    public final void registerEvents(Listener listener) {

        RaidCraft.registerEvents(listener, this);
    }

    public final void registerCommands(Class<?> clazz) {

        registerCommands(clazz, null);
    }

    public final void registerCommands(Class<?> clazz, String host) {

        if (host == null) {
            host = this.getName();
        }
        RaidCraft.getComponent(RaidCraftBasePlugin.class).trackCommand(clazz, host, null);
        commandRegistration.register(clazz);
    }

    public TranslationProvider getTranslationProvider() {

        return translationProvider;
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

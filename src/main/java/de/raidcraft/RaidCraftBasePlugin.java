package de.raidcraft;

import com.avaje.ebean.SqlUpdate;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.RaidCraft;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.tables.TCommand;
import de.raidcraft.tables.TListener;
import de.raidcraft.tables.TLog;
import de.raidcraft.tables.TPlugin;
import de.raidcraft.tables.TRcPlayer;
import de.raidcraft.util.TimeUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import javax.persistence.PersistenceException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Silthus
 */
public class RaidCraftBasePlugin extends BasePlugin implements Component, Listener {

    @Getter
    private LocalConfiguration config;
    private AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public void enable() {

        setupDatabase();
        this.config = configure(new LocalConfiguration(this));
        registerEvents(this);
        registerEvents(new RaidCraft());

        // TODO: needed?
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // kick player if server not completly started
        if (config.preLoginKicker) {
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {

                    started.set(true);
                    RaidCraft.LOGGER.info("Player login now allowed");
                }
            }, TimeUtil.secondsToTicks(config.startDelay));
        } else {
            started.set(true);
        }
    }
    @Override
    public void disable() {
        // destroy objects
        this.config = null;
    }

    private void setupDatabase() {
        try {
            // delete all commands
            SqlUpdate deleteCommands = getDatabase().createSqlUpdate("DELETE FROM rc_commands");
            deleteCommands.execute();
        } catch (PersistenceException e) {
            e.printStackTrace();
            getLogger().warning("Installing database for " + getDescription().getName() + " due to first time usage");
            installDDL();
        }
    }

    public static class LocalConfiguration extends ConfigurationBase<RaidCraftBasePlugin> {

        public LocalConfiguration(RaidCraftBasePlugin plugin) {
            super(plugin, "config.yml");
        }

        @Setting("server-start-delay")
        public double startDelay = 10.0;
        @Setting("pre-login-kicker")
        public boolean preLoginKicker = true;
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> classes = new ArrayList<>();
        classes.add(TCommand.class);
        classes.add(TRcPlayer.class);
        classes.add(TListener.class);
        classes.add(TLog.class);
        classes.add(TPlugin.class);
        return classes;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void preJoin(AsyncPlayerPreLoginEvent event) {
        if (!started.get()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                    "The server has just been started and is in the initialization phase ...");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void preJoinUUID(AsyncPlayerPreLoginEvent event) {

        UUID uuid = event.getUniqueId();
        String name = event.getName();

        TRcPlayer player = getDatabase().find(TRcPlayer.class)
                .where().eq("uuid", uuid.toString()).findUnique();
        // known player
        if (player != null) {
            // if name changed
            if (!player.getLastName().equalsIgnoreCase(name)) {
                getLogger().warning("---- NAME CHANGE FOUND (" + uuid + ") !!! ----");
                getLogger().warning("---- old name (" + player.getLastName() + ") !!! ----");
                getLogger().warning("---- new name (" + name + ") !!! ----");
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        "You changed your playername. Contact raid-craft.de to reactivate.");
            }
            return;
        }
        // new player
        player = getDatabase().find(TRcPlayer.class)
                .where().ieq("last_name", name).findUnique();
        // check if name already in use
        if (player != null) {
            getLogger().warning("---- NEW UUID FOR NAME (" + name + ") FOUND !!! ----");
            getLogger().warning("---- new uuid (" + uuid + ") FOUND !!! ----");
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    "Your playername is protected. Visit raid-craft.de for more informations");
            return;
        }
        // add new player
        player = new TRcPlayer();
        player.setLastName(name);
        player.setUuid(uuid);
        getDatabase().save(player);
    }


    /**
     * Do not call this method
     * use registerCommands(Class<?> class, String host)
     * @param clazz
     */
    public void trackCommand(Class<?> clazz, String host, String baseClass) {

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            Command anno_cmd = method.getAnnotation(Command.class);
            if (anno_cmd == null) {
                return;
            }
            NestedCommand anno_nested = method.getAnnotation(NestedCommand.class);
            if (anno_nested != null) {
                for (Class<?> childClass : anno_nested.value()) {
                    trackCommand(childClass, host, TCommand.printArray(anno_cmd.aliases()));
                }
                return;
            }
            getDatabase().save(TCommand.parseCommand(method, host, baseClass));
        }
    }
}

package de.raidcraft.api;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraftBasePlugin;
import de.raidcraft.tables.RcLogLeevel;
import de.raidcraft.tables.TListener;
import de.raidcraft.tables.TLog;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This is the static class that gives access to all basics API
 * methods and components.
 * @author Silthus
 */
public class RaidCraft implements Listener {


    /*///////////////////////////////////////////////////////////////////////////
     //                      RaidCraft RCRPG Utility Class
     ///////////////////////////////////////////////////////////////////////////*/

    public static final Logger LOGGER = Logger.getLogger("Minecraft.RaidCraft");
    private static final Map<Class<? extends Component>, Component> components = new HashMap<>();

    /**
     * Registers the given component with the RaidCraft API making it usable.
     * @param clazz to register
     * @param component instance
     */
    public static void registerComponent(Class<? extends Component> clazz, Component component) {

        components.put(clazz, component);
    }

    /**
     * Unregisters the given component class.
     * @param clazz to unregister
     */
    public static void unregisterComponent(Class<? extends Component> clazz) {

        components.remove(clazz);
    }

    /**
     * Gets the given component instance.
     * @param clazz of the component
     * @param <T> type
     * @return casted component instance
     */
    public static <T extends Component> T getComponent(Class<T> clazz) {

        return clazz.cast(components.get(clazz));
    }

    public static <T extends Event> void callEvent(T event) {

        Bukkit.getPluginManager().callEvent(event);
    }

    public static EbeanServer getDatabase(Class<? extends BasePlugin> clazz) {

        return RaidCraft.getComponent(clazz).getDatabase();
    }

    public static void registerEvents(Listener listener, Plugin plugin) {

        RaidCraftBasePlugin rPlugin = RaidCraft.getComponent(RaidCraftBasePlugin.class);
        String listenerName = listener.getClass().getName();
        String server = Bukkit.getServerName();
        TListener tListener = rPlugin.getDatabase().find(TListener.class)
                .where().eq("listener", listenerName).eq("server", server).findUnique();
        if (tListener == null) {
            tListener = new TListener();
            tListener.setListener(listenerName);
            tListener.setPlugin(plugin.getName());
            tListener.setServer(server);
            rPlugin.getDatabase().save(tListener);
        }
        tListener.setLastLoaded(new Date());
        rPlugin.getDatabase().update(tListener);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    /**
     * Intern Rc Log, saved into rc_log table
     */
    public static void info(String message, String category) {

        log(message, category, RcLogLeevel.INFO);
    }

    public static void log(String message, String category, RcLogLeevel level) {

        TLog log = new TLog();
        log.setLast(new Date());
        log.setServer(Bukkit.getServerName());
        log.setCategory(category);
        log.setLevel(level);
        log.setLog(message);
        RaidCraft.getComponent(RaidCraftBasePlugin.class).getDatabase().save(log);
    }
}

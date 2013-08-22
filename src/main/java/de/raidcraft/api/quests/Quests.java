package de.raidcraft.api.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public class Quests {

    private Quests() {}

    private static QuestProvider provider;
    private static Map<JavaPlugin, List<QuestType>> queuedTypes = new HashMap<>();
    private static Map<String, Constructor<? extends QuestTrigger>> questTrigger = new CaseInsensitiveMap<>();
    private static Map<String, JavaPlugin> triggerPlugins = new CaseInsensitiveMap<>();
    // this is just to prevent garbage collection
    private static List<QuestTrigger> loadedTrigger = new ArrayList<>();

    public static void enable(QuestProvider questProvider) {

        provider = questProvider;
        // lets register all queued quest types
        for (Map.Entry<JavaPlugin, List<QuestType>> entry : queuedTypes.entrySet()) {
            for (QuestType questType : entry.getValue()) {
                try {
                    provider.registerQuestType(entry.getKey(), questType);
                } catch (InvalidTypeException e) {
                    RaidCraft.LOGGER.warning(e.getMessage());
                }
            }
        }
        queuedTypes.clear();
    }

    public static void disable(QuestProvider questProvider) {

        provider = null;
    }

    public static boolean isEnabled() {

        return provider != null;
    }

    public static void registerQuestType(JavaPlugin plugin, QuestType questType) throws InvalidTypeException {

        if (isEnabled()) {
            provider.registerQuestType(plugin, questType);
        } else {
            if (!queuedTypes.containsKey(plugin)) {
                queuedTypes.put(plugin, new ArrayList<QuestType>());
            }
            queuedTypes.get(plugin).add(questType);
        }
    }

    public static void registerTrigger(JavaPlugin plugin, Class<? extends QuestTrigger> clazz) throws InvalidTypeException {

        if (!clazz.isAnnotationPresent(QuestTrigger.Name.class)) {
            throw new InvalidTypeException("Missing annotation on quest trigger: " + clazz.getCanonicalName());
        }
        try {
            String name = plugin.getName().toLowerCase() + "." + clazz.getAnnotation(QuestTrigger.Name.class).value();
            // check the constructor
            Constructor<? extends QuestTrigger> constructor = clazz.getDeclaredConstructor();
            questTrigger.put(name, constructor);
            triggerPlugins.put(name, plugin);
        } catch (NoSuchMethodException e) {
            throw new InvalidTypeException(e.getMessage());
        }
    }

    public static void initializeTrigger(String name, ConfigurationSection data) {

        if (!questTrigger.containsKey(name) || triggerPlugins.get(name) == null || !triggerPlugins.get(name).isEnabled()) {
            return;
        }
        try {
            // reflection time!!!
            Constructor<? extends QuestTrigger> constructor = questTrigger.get(name);
            constructor.setAccessible(true);
            QuestTrigger trigger = constructor.newInstance();
            if (trigger instanceof Listener) {
                Bukkit.getPluginManager().registerEvents((Listener) trigger, triggerPlugins.get(name));
            }
            trigger.setName(name);
            trigger.load(data);
            loadedTrigger.add(trigger);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }

    protected static void callTrigger(QuestTrigger trigger, Player player) {

        if (isEnabled()) {
            provider.callTrigger(trigger, player);
        }
    }
}

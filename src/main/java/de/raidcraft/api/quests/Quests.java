package de.raidcraft.api.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.player.QuestHolder;
import de.raidcraft.api.quests.quest.trigger.Trigger;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Silthus
 */
public class Quests {

    private Quests() {}

    private static QuestProvider provider;
    private static Map<JavaPlugin, List<QuestType>> queuedTypes = new HashMap<>();
    private static Map<String, Class<? extends QuestHost>> queuedHosts = new CaseInsensitiveMap<>();
    private static Set<QuestConfigLoader> queuedConfigLoader = new HashSet<>();
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
                    entry.getKey().getLogger().warning(e.getMessage());
                }
            }
        }
        queuedTypes.clear();
        // and all queued hosts
        for (Map.Entry<String, Class<? extends QuestHost>> entry : queuedHosts.entrySet()) {
            try {
                provider.registerQuestHost(entry.getKey(), entry.getValue());
            } catch (InvalidQuestHostException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
            }
        }
        queuedHosts.clear();
        // and all queued config loader
        for (QuestConfigLoader loader : queuedConfigLoader) {
            try {
                provider.registerQuestConfigLoader(loader);
            } catch (QuestException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
            }
        }
        queuedConfigLoader.clear();

        for(QuestTrigger trigger : loadedTrigger) {
            if(trigger instanceof Listener) {
                HandlerList.unregisterAll((Listener) trigger);
            }
        }
        questTrigger.clear();
        loadedTrigger.clear();
    }

    public static void disable(QuestProvider questProvider) {

        provider = null;
    }

    public static boolean isEnabled() {

        return provider != null;
    }

    public static void registerQuestLoader(QuestConfigLoader loader) throws QuestException {

        if (isEnabled()) {
            provider.registerQuestConfigLoader(loader);
        } else {
            if (queuedConfigLoader.contains(loader)) {
                throw new QuestException("Config loaded with same suffix is arelady registered: " + loader.getSuffix());
            }
            queuedConfigLoader.add(loader);
        }
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

    public static void registerQuestHost(JavaPlugin plugin, String type, Class<? extends QuestHost> clazz) throws InvalidQuestHostException {

        if (isEnabled()) {
            provider.registerQuestHost(type, clazz);
        } else {
            if (queuedHosts.containsKey(type)) {
                throw new InvalidQuestHostException(plugin.getName() + " tried to register already registered quest host: " + type);
            }
            queuedHosts.put(type, clazz);
        }
    }

    public static void registerTrigger(JavaPlugin plugin, Class<? extends QuestTrigger> clazz) throws InvalidTypeException {

        registerTrigger(plugin, clazz, false);
    }

    public static void registerTrigger(JavaPlugin plugin, Class<? extends QuestTrigger> clazz, boolean global) throws InvalidTypeException {

        if (!clazz.isAnnotationPresent(QuestTrigger.Name.class)) {
            throw new InvalidTypeException("Missing annotation on quest trigger: " + clazz.getCanonicalName());
        }
        try {
            String name = clazz.getAnnotation(QuestTrigger.Name.class).value();
            if (!global) {
                name = plugin.getName().toLowerCase() + "." + name;
            }
            // check the constructor
            Constructor<? extends QuestTrigger> constructor = clazz.getDeclaredConstructor(Trigger.class);
            // add all sub trigger
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(QuestTrigger.Method.class)) {
                    String methodName = name + "." + method.getAnnotation(QuestTrigger.Method.class).value();
                    questTrigger.put(methodName, constructor);
                    triggerPlugins.put(methodName, plugin);
                }
            }
            questTrigger.put(name, constructor);
            triggerPlugins.put(name, plugin);
        } catch (NoSuchMethodException e) {
            throw new InvalidTypeException(e.getMessage());
        }
    }

    public static void initializeTrigger(Trigger trigger, ConfigurationSection data) {

        String name = trigger.getName();
        if (!questTrigger.containsKey(name) || triggerPlugins.get(name) == null || !triggerPlugins.get(name).isEnabled()) {
            return;
        }
        try {
            // reflection time!!!
            Constructor<? extends QuestTrigger> constructor = questTrigger.get(name);
            constructor.setAccessible(true);
            QuestTrigger questTrigger = constructor.newInstance(trigger);
            if (questTrigger instanceof Listener) {
                Bukkit.getPluginManager().registerEvents((Listener) questTrigger, triggerPlugins.get(name));
            }
            questTrigger.load(data);
            loadedTrigger.add(questTrigger);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }

    public static QuestHolder getQuestHolder(Player player) {

        return provider.getQuestHolder(player);
    }

    public static QuestHost getQuestHost(String id) throws InvalidQuestHostException {

        return provider.getQuestHost(id);
    }

    public static List<QuestTrigger> getLoadedTrigger() {

        return new ArrayList<>(loadedTrigger);
    }
}

package de.raidcraft.api.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.host.QuestHost;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public class Quests {

    private Quests() {

    }

    private static QuestProvider provider;
    private static Map<String, Class<? extends QuestHost>> queuedHosts = new CaseInsensitiveMap<>();
    private static Set<QuestConfigLoader> queuedConfigLoader = new HashSet<>();

    public static void enable(QuestProvider questProvider) {

        provider = questProvider;
        // and all queued hosts
        for (Map.Entry<String, Class<? extends QuestHost>> entry : queuedHosts.entrySet()) {
            provider.registerQuestHost(entry.getKey(), entry.getValue());
        }
        queuedHosts.clear();
        // and all queued config loader
        for (QuestConfigLoader loader : queuedConfigLoader) {
            provider.registerQuestConfigLoader(loader);
        }
        queuedConfigLoader.clear();
    }

    public static void disable(QuestProvider questProvider) {

        provider = null;
    }

    public static boolean isEnabled() {

        return provider != null;
    }

    public static void registerQuestLoader(QuestConfigLoader loader) {

        if (isEnabled()) {
            provider.registerQuestConfigLoader(loader);
        } else {
            if (queuedConfigLoader.contains(loader)) {
                RaidCraft.LOGGER.warning("Config loaded with same suffix is arelady registered: " + loader.getSuffix());
                return;
            }
            queuedConfigLoader.add(loader);
        }
    }

    public static void registerQuestHost(JavaPlugin plugin, String type, Class<? extends QuestHost> clazz) {

        if (isEnabled()) {
            provider.registerQuestHost(type, clazz);
        } else {
            if (queuedHosts.containsKey(type)) {
                RaidCraft.LOGGER.warning(plugin.getName() + " tried to register already registered quest host: " + type);
                return;
            }
            queuedHosts.put(type, clazz);
        }
    }

    public static QuestHost getQuestHost(String id) throws InvalidQuestHostException {

        return provider.getQuestHost(id);
    }
}

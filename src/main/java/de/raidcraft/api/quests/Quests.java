package de.raidcraft.api.quests;

import de.raidcraft.RaidCraft;
import org.bukkit.plugin.java.JavaPlugin;

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
}

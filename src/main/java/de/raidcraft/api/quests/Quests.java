package de.raidcraft.api.quests;

import de.raidcraft.RaidCraft;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Silthus
 */
public class Quests {

    private Quests() {

    }

    private static QuestProvider provider;
    private static Set<QuestConfigLoader> queuedConfigLoader = new HashSet<>();

    public static void enable(QuestProvider questProvider) {

        provider = questProvider;
        // and all queued config loader
        queuedConfigLoader.forEach(provider::registerQuestConfigLoader);
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

    @Nullable
    public static QuestConfigLoader getQuestConfigLoader(String suffix) {

        return provider.getQuestConfigLoader(suffix);
    }

    public static Optional<QuestProvider> getQuestProvider() {

        return Optional.ofNullable(provider);
    }
}

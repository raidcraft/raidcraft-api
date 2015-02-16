package de.raidcraft.api.quests;

import de.raidcraft.api.quests.host.QuestHost;

/**
 * @author Silthus
 */
public interface QuestProvider {

    public void registerQuestHost(String type, Class<? extends QuestHost> clazz);

    public void registerQuestConfigLoader(QuestConfigLoader loader);

    public QuestHost getQuestHost(String id) throws InvalidQuestHostException;
}

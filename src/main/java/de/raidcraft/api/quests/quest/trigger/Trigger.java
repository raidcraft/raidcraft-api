package de.raidcraft.api.quests.quest.trigger;

import de.raidcraft.api.quests.player.QuestHolder;
import de.raidcraft.api.quests.quest.QuestTemplate;
import de.raidcraft.api.quests.quest.action.Action;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * @deprecated see {@link de.raidcraft.api.action.trigger.Trigger}
 */
@Deprecated
public interface Trigger {

    public enum Type {

        QUEST_START,
        QUEST_OBJECTIVE,
        QUEST_ACCEPTED
    }

    public int getId();

    public String getName();

    public Type getType();

    public long getDelay();

    public ConfigurationSection getConfig();

    public QuestTemplate getQuestTemplate();

    public List<TriggerListener> getListeners();

    public void registerListener(TriggerListener listener);

    public void unregisterListener(TriggerListener listener);

    public List<Action<Trigger>> getActions();

    public void trigger(QuestHolder holder);
}

package de.raidcraft.api.quests.holder;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.api.quests.quest.Quest;
import de.raidcraft.api.quests.quest.QuestTemplate;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Silthus
 */
public interface QuestHolder {

    public int getId();

    public String getName();

    public Player getPlayer();

    public default boolean hasCompletedQuest(String quest) {

        try {
            return hasQuest(quest) && getQuest(quest).isCompleted();
        } catch (QuestException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
        return false;
    }

    public default boolean hasCompletedQuest(QuestTemplate questTemplate) {

        Quest quest = getQuest(questTemplate);
        return quest != null && quest.isCompleted();
    }

    public boolean hasQuest(String quest);

    public default boolean hasQuest(QuestTemplate template) {

        return hasQuest(template.getId());
    }

    public boolean hasActiveQuest(String quest);

    public default boolean hasActiveQuest(QuestTemplate template) {

        return hasActiveQuest(template.getId());
    }

    public Quest getQuest(String quest) throws QuestException;

    @Nullable
    public Quest getQuest(QuestTemplate questTemplate);

    public List<Quest> getAllQuests();

    public List<Quest> getCompletedQuests();

    public List<Quest> getActiveQuests();

    public default void sendMessage(String text) {

        if (text == null || text.equals("")) return;
        getPlayer().sendMessage(text);
    }

    public void addQuest(Quest quest);

    public void abortQuest(Quest quest);

    public Quest createQuest(QuestTemplate template);

    public Quest startQuest(QuestTemplate template) throws QuestException;

    public void save();
}

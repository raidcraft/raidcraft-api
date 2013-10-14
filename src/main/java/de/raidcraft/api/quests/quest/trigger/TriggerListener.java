package de.raidcraft.api.quests.quest.trigger;

import de.raidcraft.api.quests.player.QuestHolder;

/**
 * @author Silthus
 */
public interface TriggerListener {

    public void trigger(QuestHolder player);
}

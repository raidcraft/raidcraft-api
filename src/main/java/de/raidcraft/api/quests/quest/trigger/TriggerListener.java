package de.raidcraft.api.quests.quest.trigger;

import de.raidcraft.api.quests.player.QuestHolder;

/**
 * @deprecated see {@link de.raidcraft.api.action.trigger.Trigger}
 */
@Deprecated
public interface TriggerListener {

    public void trigger(QuestHolder player);
}

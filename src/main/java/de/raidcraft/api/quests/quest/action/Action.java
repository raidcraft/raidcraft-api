package de.raidcraft.api.quests.quest.action;

import de.raidcraft.api.quests.QuestException;
import de.raidcraft.api.quests.player.QuestHolder;

/**
 * @author Silthus
 */
public interface Action<T> {

    public int getId();

    public String getName();

    public T getProvider();

    public boolean isExecuteOnce();

    public long getDelay();

    public long getCooldown();

    public void execute(QuestHolder player, T holder) throws QuestException;

    public void save();
}

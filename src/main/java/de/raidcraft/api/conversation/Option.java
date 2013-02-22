package de.raidcraft.api.conversation;

import java.util.List;

/**
 * @author Silthus
 */
public interface Option<T> {

    public Stage<T> getStage();

    public int getIndex();

    public String getText();

    public List<String> getAliases();

    public List<Action<T>> getActions();

    public void choose(RunningConversation<T> conversation);
}

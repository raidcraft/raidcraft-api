package de.raidcraft.api.conversation;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface Conversation<T> {

    public String getName();

    public Collection<Stage<T>> getStages();

    public Stage<T> getStage(String name);

    public Stage<T> getStartStage();

    public RunningConversation<T> start(T player);
}

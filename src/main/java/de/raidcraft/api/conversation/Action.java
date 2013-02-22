package de.raidcraft.api.conversation;

/**
 * @author Silthus
 */
public interface Action<T> {

    public String getName();

    public Stage<T> getStage();

    public void execute(RunningConversation<T> conversation);
}

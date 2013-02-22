package de.raidcraft.api.conversation;

/**
 * @author Silthus
 */
public interface RunningConversation<T> {

    public Conversation getConversation();

    public T getConversationPartner();

    public Stage<T> getCurrentStage();

    public void setCurrentStage(Stage<T> stage);

    public void end(Stage<T> stage);
}

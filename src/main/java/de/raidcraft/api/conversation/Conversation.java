package de.raidcraft.api.conversation;

import java.util.List;

/**
 * @author Silthus
 */
public interface Conversation {

    /**
     * Gets the name of the conversation.
     *
     * @return name of the conversation
     */
    public String getName();

    public String getPlayerName();

    /**
     * Gets a recursive list of all dialogues and their sub options.
     *
     * @return list of all dialogues contained in this conversation
     */
    public List<Dialogue> getDialogues();

    /**
     * Gets the initial {@link Dialogue} this conversation will start at.
     *
     * @return Dialogue that is triggered when this conversation starts.
     */
    public Dialogue getInitialDialogue();

    /**
     * Gets the dialogue the player is currently at.
     *
     * @return dialogue the player
     * @throws ConversationException if the player did not start the conversation yet
     */
    public Dialogue getCurrentDialogue() throws ConversationException;

    /**
     * Sets the current dialogue the player is at.
     */
    public void setCurrentDialogue(Dialogue currentDialogue);

    /**
     * Starts the conversation and triggers the first dialogue.
     *
     * @return Dialogue that starts the conversation.
     */
    public Dialogue start();

    /**
     * Starts the conversation at the given dialogue. The dialogue can be
     * retrieved by calling the
     *
     * @param dialogue object to start the conversation at
     * @return the dialogue passed in to start at
     */
    public Dialogue startAt(Dialogue dialogue);

    /**
     * Stops the conversation and returns the dialogue the player was at when stopped.
     *
     * @return Dialogue the player was on
     */
    public Dialogue stop();
}

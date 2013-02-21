package de.raidcraft.api.conversation;

import java.util.List;

/**
 * @author Silthus
 */
public interface Dialogue {

    public String getName();

    /**
     * Gets the conversation that started this dialogue.
     *
     * @return Conversation that started the dialogue
     */
    public Conversation getConversation();

    /**
     * Null if this is the first dialogue (usually after the start of a conversation).
     * Will hold the parent of the dialogue that chose this dialogue to trigger.
     *
     * @return null if dialogue was triggered by a conversation
     */
    public Dialogue getParent();

    /**
     * Gets all child dialogues or also known as options the player can progress on.
     *
     * @return list of options the player can choose
     */
    public List<Dialogue> getOptions();

    /**
     * Chooses an option progressing the player to the next dialogue.
     *
     * @param id of the option
     * @return next dialogue
     */
    public Dialogue choose(int id) throws InvalidDialogueException;

    /**
     * If possible (parent != null) the player will return to his last dialogue
     * and can choose again.
     *
     * @return the dialogue the player can return to
     */
    public Dialogue back() throws InvalidDialogueException;

    /**
     * Gets the text that is displayed before the options.
     *
     * @return text to display before options
     */
    public String[] getIntroText();

    /**
     * Returns the screen to display the player including the options, exit and back choice.
     *
     * @return text that can be displayed to the player
     */
    public String[] getCompleteText();
}

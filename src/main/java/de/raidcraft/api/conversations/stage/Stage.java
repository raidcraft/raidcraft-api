package de.raidcraft.api.conversations.stage;

import de.raidcraft.api.action.action.ActionHolder;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.conversation.Conversation;

import java.util.List;
import java.util.Optional;

/**
 * @author mdoering
 */
public interface Stage extends ActionHolder {

    default String getIdentifier() {

        return getTemplate().getIdentifier();
    }

    /**
     * Gets the stage template that was created when the plugin loaded.
     *
     * @return template of the stage
     */
    StageTemplate getTemplate();

    /**
     * Gets the conversation that is attached to this stage.
     *
     * @return conversation
     */
    Conversation getConversation();

    /**
     * @see StageTemplate#getText()
     */
    default Optional<String[]> getText() {

        return getTemplate().getText();
    }

    /**
     * Gets the answer based on the players input. If no answer is found
     * an empty optional will be returned.
     *
     * @param input to process
     * @return optional answer
     */
    Optional<Answer> getAnswer(String input);

    /**
     * Gets all answers of this stage.
     *
     * @return list of answers
     */
    List<Answer> getAnswers();

    /**
     * Clears all answers of this stage.
     *
     * @return the current stage
     */
    Stage clearAnswers();

    /**
     * Adds the given answer to the stage.
     *
     * @param answer to add
     * @return this stage
     */
    Stage addAnswer(Answer answer);

    /**
     * Changes the current page to the given page index.
     * If the page does not exist the next existing page will be set.
     *
     * @param page to change to
     * @return false if page could not be changed
     */
    boolean changePage(int page);

    /**
     * Triggers this stage displaying the text and executing all actions.
     *
     * @return triggered stage
     */
    Stage trigger();

    /**
     * Triggers this stage displaying the text and executing all actions.
     *
     * @param executeActions if false no actions will be executed
     * @return triggered stage
     */
    de.raidcraft.api.conversations.stage.Stage trigger(boolean executeActions);
}

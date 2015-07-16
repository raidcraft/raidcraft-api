package de.raidcraft.api.conversations.stage;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ActionHolder;
import de.raidcraft.api.action.requirement.RequirementHolder;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;

import java.util.List;
import java.util.Optional;

/**
 * @author mdoering
 */
public interface Stage extends ActionHolder, RequirementHolder {

    static Stage of(Conversation conversation, String text, Answer... answers) {

        return Conversations.createStage(conversation, text, answers);
    }

    static Stage confirm(Conversation conversation, String text, Action... successActions) {

        return confirm(conversation, text, false, successActions);
    }

    static Stage confirm(Conversation conversation, String text, boolean abortToOldStage, Action... successActions) {

        Stage stage = Conversations.createStage(conversation, text)
                .addAnswer(
                        Answer.of("Ja, ich bin mir sicher.")
                                .addActions(successActions));
        if (abortToOldStage) {
            stage.addAnswer(
                    Answer.of("Nein, zeig mir nochmal meine Optionen.")
                            .addAction(Action.changeStage(conversation.getStageHistory().peek())));
        } else {
            stage.addAnswer(
                    Answer.of("Nein, ich habe es mir anders überlegt.")
                            .addAction(Action.endConversation(ConversationEndReason.ENDED))
            );
        }
        return stage;
    }

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
     * @see StageTemplate#getRandomActions()
     */
    List<Action<?>> getRandomActions();

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
     * Shows all possible answers in the chat.
     *
     * @return this stage
     */
    Stage showAnswers();

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
    Stage trigger(boolean executeActions);

    /**
     * Aborts the execution of all currently running actions.
     * Will cascade down into all active answers.
     */
    void abortActionExecution();

    /**
     * Changes to the given stage, calling {@link Conversation#changeToStage(Stage)}
     *
     * @return this stage
     */
    default Stage changeTo() {

        getConversation().changeToStage(this);
        return this;
    }
}

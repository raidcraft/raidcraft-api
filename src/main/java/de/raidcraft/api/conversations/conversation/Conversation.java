package de.raidcraft.api.conversations.conversation;

import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.conversations.stage.Stage;
import de.raidcraft.api.conversations.stage.StageTemplate;
import de.raidcraft.util.fanciful.FancyMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

/**
 * @author mdoering
 */
public interface Conversation extends ConfigurationSection {

    String DEFAULT_TYPE = "default";

    /**
     * Gets the unique identifier of the conversation.
     *
     * @return unique identifier
     */
    default String getIdentifier() {

        return getTemplate().getIdentifier();
    }

    /**
     * Gets the player involved in this conversation.
     *
     * @return player involved in the conversation
     */
    Player getOwner();

    /**
     * Gets the host that started the conversation.
     *
     * @return conversation host
     */
    ConversationHost getHost();

    /**
     * Gets the {@link ConversationTemplate} of this Conversation.
     *
     * @return conversation template
     */
    ConversationTemplate getTemplate();

    /**
     * Triggers the current stage. If not current stage is found the start stage will be triggered.
     *
     * @return true if the stage was triggerd, false if no stage was found
     */
    boolean triggerCurrentStage();

    /**
     * Aborts the execution of all currently running actions.
     * Will cascade down into the current active stage and all answers.
     */
    void abortActionExection();

    /**
     * True if action execution of this conversation should be aborted.
     *
     * @return true if execution of actions should be aborted
     */
    boolean isAbortActionExecution();

    /**
     * Gets the current active stage of the conversation.
     *
     * @return current stage
     */
    Optional<Stage> getCurrentStage();

    /**
     * Gets a list of previous stages in this conversation.
     * The last stage is always on top and the first stage is the one on the bottom.
     *
     * @return stage history from most recent to first
     */
    Stack<Stage> getStageHistory();

    /**
     * Sets the current stage to the given stage.
     *
     * @param stage to set
     * @return the current conversation
     */
    Conversation setCurrentStage(Stage stage);

    /**
     * Changes the conversation to the given stage.
     *
     * @param stage to  change to
     * @return current conversation
     */
    Conversation changeToStage(Stage stage);

    /**
     * Gets a list of all stages attached to this conversation.
     *
     * @return list of stages
     */
    List<StageTemplate> getStages();

    /**
     * Gets the given stage by its identifier.
     * If no stage is found an empty optional will be returned.
     *
     * @param identifier of the stage
     * @return optional stage
     */
    Optional<Stage> getStage(String identifier);

    /**
     * Adds the given stage to the conversation.
     *
     * @param stage to add
     * @return this conversation
     */
    Conversation addStage(StageTemplate stage);

    /**
     * Answers the conversation with the given text. Tries to find a valid answer
     * and executes all actions of the answer if it was found.
     *
     * @param answer to process
     * @return executed answer
     */
    default Optional<Answer> answer(String answer) {

        return answer(answer, true);
    }

    /**
     * Answers the conversation with the given text. Tries to find a valid answer
     * and executes all actions of the answer if it was found and executeActions is true.
     *
     * @param answer to process
     * @param executeActions true if actions should be processed
     * @return executed answer
     */
    Optional<Answer> answer(String answer, boolean executeActions);

    /**
     * Gets the last input of this conversation if an input answer was answered.
     *
     * @return optional input if input answer was answered
     */
    Optional<String> getLastInput();

    /**
     * Sets the last input of this conversation.
     *
     * @param input to set
     */
    void setLastInput(String input);

    /**
     * @see Stage#changePage(int)
     */
    boolean changePage(int page);

    void setGlobal(String key, Object value);

    /**
     * Starts this conversation by setting the current {@link Stage} to the {@link StageTemplate#START_STAGE} and calling
     * {@link this#triggerCurrentStage()}.
     * If the conversation was saved it will restart at the last active stage.
     *
     * @return true if the conversation started, false if no stage was found
     */
    boolean start();

    /**
     * Ends the current conversation returning the stage the conversation ended at.
     * Will be an empty optional if conversation was not started and not active stage is present.
     *
     * @param reason why the conversation ended
     * @return last active stage if conversation was started, otherwise an empty optional
     */
    Optional<Stage> end(ConversationEndReason reason);

    /**
     * Aborts the current conversation saving the progress if {@link ConversationTemplate#isPersistant()} is true.
     *
     * @param reason why the conversation was aborted
     * @return last active stage of the conversation
     */
    Optional<Stage> abort(ConversationEndReason reason);

    /**
     * Sends the given conversation to the entity listening to this conversation.
     *
     * @param lines to send
     * @return this conversation
     */
    Conversation sendMessage(String... lines);

    /**
     * Sends the given conversation to the entity listening to this conversation.
     *
     * @param lines to send
     * @return this conversation
     */
    Conversation sendMessage(FancyMessage... lines);

    /**
     * Saves the current conversation to a persistant storage.
     * Saved conversations can be resumed when the conversation is started again.
     */
    void save();
}

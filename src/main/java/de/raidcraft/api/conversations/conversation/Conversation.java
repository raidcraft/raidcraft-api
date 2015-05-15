package de.raidcraft.api.conversations.conversation;

import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.conversations.stage.Stage;
import de.raidcraft.api.conversations.stage.StageTemplate;
import mkremins.fanciful.FancyMessage;

import java.util.List;
import java.util.Optional;

/**
 * @author mdoering
 */
public interface Conversation<T> {

    /**
     * Gets the unique identifier of the conversation.
     *
     * @return unique identifier
     */
    default String getIdentifier() {

        return getTemplate().getIdentifier();
    }

    /**
     * Gets the entity involved in this conversation.
     *
     * @return entity involved in the conversation
     */
    T getEntity();

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
     * Gets the current active stage of the conversation.
     *
     * @return current stage
     */
    Optional<Stage> getCurrentStage();

    /**
     * Sets the current stage to the given stage.
     *
     * @param stage to set
     * @return the current conversation
     */
    Conversation<T> setCurrentStage(Stage stage);

    /**
     * Gets a list of all stages attached to this conversation.
     *
     * @return list of stages
     */
    List<Stage> getStages();

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
    Conversation<T> addStage(Stage stage);

    /**
     * Starts this conversation by setting the current {@link Stage} to the {@link StageTemplate#START_STAGE} and calling
     * {@link this#triggerCurrentStage()}.
     *
     * @return true if the conversation started, false if not stage was found
     */
    boolean start();

    /**
     * Sends the given conversation to the entity listening to this conversation.
     *
     * @param lines to send
     * @return this conversation
     */
    Conversation<T> sendMessage(String... lines);

    /**
     * Sends the given conversation to the entity listening to this conversation.
     *
     * @param lines to send
     * @return this conversation
     */
    Conversation<T> sendMessage(FancyMessage... lines);
}

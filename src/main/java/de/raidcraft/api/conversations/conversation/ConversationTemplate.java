package de.raidcraft.api.conversations.conversation;

import de.raidcraft.api.action.action.ActionHolder;
import de.raidcraft.api.action.requirement.RequirementHolder;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.conversations.stage.StageTemplate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

/**
 * @author mdoering
 */
public interface ConversationTemplate extends RequirementHolder, ActionHolder, Comparable<ConversationTemplate> {

    String DEFAULT_CONVERSATION_TEMPLATE = "default";

    /**
     * Gets the unique identifier of the conversation.
     *
     * @return unique identifier
     */
    String getIdentifier();

    /**
     * Gets the conversation type that should be instaniated when starting a conversation.
     *
     * @return conversation type - defaults to {@link Conversation#DEFAULT_TYPE}
     */
    String getConversationType();

    /**
     * Gets the priority of the conversation. A higher priority is relevant for the chosen
     * default conversation if multiple exist in a host and all requirements match.
     *
     * @return conversation priority
     */
    int getPriority();

    /**
     * If the conversation is persistent, the current state of the conversation will be saved
     * when the conversation is aborted.
     *
     * @return true if conversation will save if it is aborted
     */
    boolean isPersistent();

    /**
     * If the conversation is auto ending the conversation will silently end when no extra stages
     * besides the startStage stage are defined.
     *
     * @return true if conversation will auto end
     */
    boolean isAutoEnding();

    /**
     * Blocks the startStage of other conversations if this conversation is active.
     *
     * @return true if conversation should block the startStage of other conversations
     */
    boolean isBlockingConversationStart();

    /**
     * Aborts the conversation when the player runs out of range of the host.
     *
     * @return false if conversation should not end when running out of range
     */
    boolean isEndingOutOfRange();

    /**
     * If true the conversation cannot be exited by typing "exit" or any other end keyword.
     * {@link #isEndingOutOfRange()} will also be false if not otherwise set to true.
     *
     * @return false if conversation cannot be ended by the player
     */
    boolean isExitable();

    /**
     * The implementation of this method depends on the conversation template.
     * May load the provided config into the conversation template and may do nothing.
     *
     * @param config to load
     */
    void loadConfig(ConfigurationSection config);

    /**
     * Gets the {@link ConfigurationSection} that defines special host settings that are defined in
     * the {@link ConversationTemplate}.
     *
     * @return host setting
     */
    ConfigurationSection getHostSettings();

    /**
     * Gets all stages registered in this conversation template.
     *
     * @return registered stages
     */
    Map<String, StageTemplate> getStages();

    /**
     * Gets the given stage by its displayName.
     * If no stage is found an empty optional will be returned.
     *
     * @param name of the stage
     * @return optional stage template
     */
    Optional<StageTemplate> getStage(String name);

    /**
     * Adds the given stage to the conversation.
     * If a stage with the same identifier already exists it will return the already existing stage.
     * If the existing stage does not match {@link TStage} it will override the existing stage
     * and add the passed in {@param stageTemplate}.
     *
     * @param stageTemplate to add to the conversation.
     * @param <TStage>      type of the stage template
     * @return added stage or existing stage with same identifier if {@link TStage} matches.
     */
    <TStage extends StageTemplate> TStage addStage(TStage stageTemplate);

    /**
     * Creates a new conversation without starting it.
     * Call {@link de.raidcraft.api.conversations.conversation.Conversation#start()} to startStage the conversation.
     * Make sure to keep a reference to the conversation as it will not persist in cache.
     *
     * @param player to create conversation for
     * @param host that started the conversation
     * @return started conversation
     */
    Conversation createConversation(Player player, ConversationHost host);

    /**
     * Starts this conversation for the given player caching the active conversation
     * in {@link Conversations#setActiveConversation(Conversation)}. If the player already has an
     * active conversation the conversation will be aborted with the {@link ConversationEndReason#START_NEW_CONVERSATION}.
     *
     * @param player to startStage conversation for
     * @param host that is hosting this conversation
     * @return started conversation
     */
    Conversation startConversation(Player player, ConversationHost host);

    /**
     * Starts this conversation for the given player and host with the given {@link StageTemplate}.
     *
     * @param player to startStage conversation for
     * @param host that is hosting this conversation
     * @param stage to startStage at
     * @return started conversation
     */
    Conversation startConversation(Player player, ConversationHost host, StageTemplate stage);
}

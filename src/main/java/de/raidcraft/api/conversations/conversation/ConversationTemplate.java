package de.raidcraft.api.conversations.conversation;

import de.raidcraft.api.action.requirement.RequirementHolder;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.conversations.stage.StageTemplate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

/**
 * @author mdoering
 */
public interface ConversationTemplate extends RequirementHolder, Comparable<ConversationTemplate> {

    String DEFAULT_CONVERSATION_TEMPLATE = "default";

    /**
     * Gets the unique identifier of the conversation.
     *
     * @return unique identifier
     */
    String getIdentifier();

    /**
     * Gets the priority of the conversation. A higher priority is relevant for the chosen
     * default conversation if multiple exist in a host and all requirements match.
     *
     * @return conversation priority
     */
    int getPriority();

    /**
     * If the conversation is persistant, the current state of the conversation will be saved
     * when the conversation is aborted.
     *
     * @return true if conversation will save if it is aborted
     */
    boolean isPersistant();

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
    List<StageTemplate> getStages();

    /**
     * Gets the given stage by its name.
     * If no stage is found an empty optional will be returned.
     *
     * @param name of the stage
     * @return optional stage template
     */
    Optional<StageTemplate> getStage(String name);

    /**
     * Creates a new conversation without starting it.
     * Call {@link de.raidcraft.api.conversations.conversation.Conversation#start()} to start the conversation.
     * Make sure to keep a reference to the conversation as it will not persist in cache.
     *
     * @param player to create conversation for
     * @param host that started the conversation
     * @return started conversation
     */
    Conversation<Player> createConversation(Player player, ConversationHost host);

    /**
     * Starts this conversation for the given player caching the active conversation
     * in {@link Conversations#setActiveConversation(Conversation)}. If the player already has an
     * active conversation the conversation will be aborted with the {@link ConversationEndReason#START_NEW_CONVERSATION}.
     *
     * @param player to start conversation for
     * @param host that is hosting this conversation
     * @return started conversation
     */
    Conversation<Player> startConversation(Player player, ConversationHost host);
}

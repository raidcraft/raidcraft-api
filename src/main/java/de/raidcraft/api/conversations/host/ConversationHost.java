package de.raidcraft.api.conversations.host;

import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author mdoering
 */
public interface ConversationHost<T> {

    UUID getUniqueId();

    T getType();

    Optional<String> getName();

    /**
     * Loads additional information from the configuration if present.
     * This could be equipment, or sign positioning, etc.
     *
     * @param config to load from
     */
    void load(ConfigurationSection config);

    /**
     * Gets the current location of the conversation host.
     * Usually the conversation will abort if the player is too far aways from the host.
     *
     * @return host location
     */
    Location getLocation();

    /**
     * Adds the given conversation to the list of default conversations of this host.
     * All conversations of this host will be sorted using {@link ConversationTemplate#compareTo(Object)}.
     *
     * @param conversationTemplate to add as default conversation
     */
    void addDefaultConversation(ConversationTemplate conversationTemplate);

    /**
     * Gets the default conversation of this conversation host. If no default
     * conversation exists an empty {@link Optional} will be returned.
     * A host can have multiple default conversations and picks the one that will meet all
     * {@link de.raidcraft.api.action.requirement.Requirement}s. If more than one conversation
     * matches the requirements the one with the higher priority or order will be returned.
     *
     * @return optional default conversation
     */
    Optional<ConversationTemplate> getDefaultConversation();

    /**
     * Gets a list of all possible default conversations.
     *
     * @return list of default conversations
     */
    List<ConversationTemplate> getDefaultConversations();

    /**
     * Gets the saved {@link ConversationTemplate} for the player or the
     * default conversation if no saved Conversation exists.
     * The retrieved conversation will be based on the priority of the conversation where a
     * saved player conversation is always of higher priority than the default conversations.
     *
     * @param player to get {@link ConversationTemplate} for
     * @return optional saved conversation or default conversation
     */
    Optional<ConversationTemplate> getConversation(@NonNull Player player);

    /**
     * Gets a list of all saved player conversations registered with this host.
     *
     * @param player to get conversations for
     * @return list of all saved conversations
     */
    List<ConversationTemplate> getPlayerConversations(@NonNull Player player);

    /**
     * Sets the given {@link Conversation} for the player. The
     * Conversation will persist until {@link #unsetConversation(Player, ConversationTemplate)} is called.
     *
     * @param player to set persistant conversation for
     * @param conversation to set
     */
    default void setConversation(@NonNull Player player, @NonNull ConversationTemplate conversation) {

        setConversation(player.getUniqueId(), conversation);
    }

    /**
     * Sets the given {@link Conversation} for the player. The
     * Conversation will persist until {@link #unsetConversation(Player, ConversationTemplate)} is called.
     *
     * @param player to set persistant conversation for
     * @param conversation to set
     */
    void setConversation(@NonNull UUID player, @NonNull ConversationTemplate conversation);

    /**
     * Unsets the given conversation for the player. Will set the conversation to the default conversation.
     *
     * @param player to unset conversation for
     * @param conversation to remove
     * @return true if the conversation was found and removed
     */
    boolean unsetConversation(@NonNull Player player, @NonNull ConversationTemplate conversation);

    /**
     * Gets the conversation from {@link #getConversation(Player)} and calls {@link ConversationTemplate#startConversation(Player, ConversationHost)}.
     *
     * @param player to start conversation for
     * @return started conversation
     */
    Optional<Conversation> startConversation(Player player);

    /**
     * Starts the given {@link Conversation} for the given {@link Player}
     *
     * @param player to start conversation for
     * @param conversation to start
     * @return started conversation
     */
    Optional<Conversation> startConversation(Player player, String conversation);
}

package de.raidcraft.api.conversations.host;

import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author mdoering
 */
public interface ConversationHost {

    UUID getUniqueId();

    /**
     * Gets the current location of the conversation host.
     * Usually the conversation will abort if the player is too far aways from the host.
     *
     * @return host location
     */
    Location getLocation();

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
     *
     * @param player to get {@link ConversationTemplate} for
     * @return optional saved conversation or default conversation
     */
    Optional<ConversationTemplate> getConversation(@NonNull Player player);

    /**
     * Sets the given {@link Conversation} for the player. The
     * Conversation will persist until it is unset.
     *
     * @param player to set persistant conversation for
     * @param conversation to set
     */
    void setConversation(@NonNull Player player, @NonNull ConversationTemplate conversation);

    /**
     * Sets the given {@link Conversation} for the player.
     * If persist is set to false the Conversation will be removed after it was finished once.
     *
     * @param player to set conversation for
     * @param conversation to set
     * @param persistant false if conversation should be unset after it completed once
     */
    void setConversation(@NonNull Player player, @NonNull ConversationTemplate conversation, boolean persistant);

    /**
     * Unsets the current conversation for the player. Will set the conversation to the default conversation.
     *
     * @param player to unset conversation for
     * @return optional conversation if a conversation was removed
     */
    Optional<ConversationTemplate> unsetConversation(@NonNull Player player);
}

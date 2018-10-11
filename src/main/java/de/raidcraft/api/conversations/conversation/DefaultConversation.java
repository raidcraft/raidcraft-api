package de.raidcraft.api.conversations.conversation;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.util.ConfigUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultConversation {

    /**
     * Parses the given {@link ConfigurationSection} for one or multiple mappings between
     * a {@link ConversationHost} and a {@link ConversationTemplate}.
     * <br>
     * <example>
     *     # Pass this section into the method for parsing.
     *     default-convs:
     *       - 'host-name:conversation-name'
     *       - 'second-host:same-conversation'
     * </example>
     *
     * @param defaultConvs list of strings to parse
     * @return list of {@link DefaultConversation} or empty list of no mappings were found
     */
    public static Collection<DefaultConversation> fromConfig(List<String> defaultConvs) {

        ArrayList<DefaultConversation> conversations = new ArrayList<>();
        if (defaultConvs == null) return conversations;

        for (String conv : defaultConvs) {
            String[] split = conv.split(":");
            if (split.length < 2) continue;
            create(split[0], split[1]).ifPresent(conversations::add);
        }

        return conversations;
    }

    /**
     * Creates a new default conversation mapping from the given hostId and conversationId.
     * The method will validate if both inputs are valid and only create a default conversation for valid hosts and conversations.
     *
     * @param hostId to map conversation to
     * @param conversationId to map to host
     * @return valid default conversation or empty optional
     */
    public static Optional<DefaultConversation> create(String hostId, String conversationId) {

        Optional<ConversationHost<?>> conversationHost = Conversations.getConversationHost(hostId);
        Optional<ConversationTemplate> conversationTemplate = Conversations.getConversationTemplate(conversationId);

        if (!conversationHost.isPresent()) {
            RaidCraft.LOGGER.warning("Invalid default conversation mapping with host " + hostId);
            return Optional.empty();
        }

        if (!conversationTemplate.isPresent()) {
            RaidCraft.LOGGER.warning("Invalid default conversation mapping with host " + hostId
                    + " and conversation " + conversationId);
            return Optional.empty();
        }

        return Optional.of(new DefaultConversation(conversationHost.get(), conversationTemplate.get()));
    }

    private final ConversationHost host;
    private final ConversationTemplate conversationTemplate;

    /**
     * Sets the default conversation on the host for the given {@link Player}.
     *
     * @param player to set default conversation for
     */
    public void setConversation(Player player) {
        setConversation(player.getUniqueId());
    }

    /**
     * Sets the default conversation for a player with the given {@link UUID}.
     * Works for offline players as well as online players.
     *
     * @param playerId to set default conversation for
     */
    public void setConversation(UUID playerId) {
        getHost().setConversation(playerId, getConversationTemplate());
    }

    /**
     * Removes the default conversation from the host for the player.
     *
     * @param player to remove this default conversation for
     */
    public void unsetConversation(Player player) {
        unsetConversation(player.getUniqueId());
    }

    /**
     * Removes the default conversation from the host for the given player with the id.
     * Works for offline players as well as online players.
     *
     * @param playerId to remove this default conversation for
     */
    public void unsetConversation(UUID playerId) {
        getHost().unsetConversation(playerId, getConversationTemplate());
    }

    /**
     * Clears all set default conversations for the given player from the host.
     *
     * @param player to clear default conversation for
     */
    public void clearConversations(Player player) {
        getHost().clearConversation(player);
    }
}

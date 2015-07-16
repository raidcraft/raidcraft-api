package de.raidcraft.api.conversations.conversation;

import de.raidcraft.api.conversations.Conversations;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

/**
 * @author mdoering
 */
@FunctionalInterface
public interface ConversationVariable {

    static Optional<String> getString(Player player, String key) {

        Optional<Conversation> activeConversation = Conversations.getActiveConversation(player);
        if (!activeConversation.isPresent()) return Optional.empty();
        if (!activeConversation.get().isSet(key)) return Optional.empty();
        return Optional.of(activeConversation.get().getString(key));
    }

    static Optional<Integer> getInt(Player player, String key) {

        Optional<Conversation> activeConversation = Conversations.getActiveConversation(player);
        if (!activeConversation.isPresent()) return Optional.empty();
        if (!activeConversation.get().isSet(key)) return Optional.empty();
        return Optional.of(activeConversation.get().getInt(key));
    }

    static Optional<Boolean> getBoolean(Player player, String key) {

        Optional<Conversation> activeConversation = Conversations.getActiveConversation(player);
        if (!activeConversation.isPresent()) return Optional.empty();
        if (!activeConversation.get().isSet(key)) return Optional.empty();
        return Optional.of(activeConversation.get().getBoolean(key));
    }

    static Optional<Double> getDouble(Player player, String key) {

        Optional<Conversation> activeConversation = Conversations.getActiveConversation(player);
        if (!activeConversation.isPresent()) return Optional.empty();
        if (!activeConversation.get().isSet(key)) return Optional.empty();
        return Optional.of(activeConversation.get().getDouble(key));
    }

    static Optional<Long> getLong(Player player, String key) {

        Optional<Conversation> activeConversation = Conversations.getActiveConversation(player);
        if (!activeConversation.isPresent()) return Optional.empty();
        if (!activeConversation.get().isSet(key)) return Optional.empty();
        return Optional.of(activeConversation.get().getLong(key));
    }

    static Optional<List<String>> getStringList(Player player, String key) {

        Optional<Conversation> activeConversation = Conversations.getActiveConversation(player);
        if (!activeConversation.isPresent()) return Optional.empty();
        if (!activeConversation.get().isSet(key)) return Optional.empty();
        return Optional.of(activeConversation.get().getStringList(key));
    }

    static void set(Player player, String key, Object value) {

        Optional<Conversation> activeConversation = Conversations.getActiveConversation(player);
        if (activeConversation.isPresent()) {
            activeConversation.get().set(key, value);
        }
    }

    String replace(Conversation conversation);
}

package de.raidcraft.api.conversations.legacy;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Silthus
 */
public abstract class AbstractConversationHost implements ConversationHost {

    private final String name;
    private final String defaultConversationName;
    private final Map<UUID, String> playerConversations = new HashMap<>();

    public AbstractConversationHost(String name, String defaultConversationName) {

        this.name = name;
        this.defaultConversationName = defaultConversationName;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getDefaultConversationName() {

        return defaultConversationName;
    }

    @Override
    public void setConversation(Player player, String conversation) {

        playerConversations.put(player.getUniqueId(), conversation);
    }

    @Override
    public String getConversation(Player player) {

        if (playerConversations.containsKey(player.getUniqueId())) {
            return playerConversations.get(player.getUniqueId());
        }
        return defaultConversationName;
    }
}

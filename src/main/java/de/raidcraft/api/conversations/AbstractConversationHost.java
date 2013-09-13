package de.raidcraft.api.conversations;

import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author Silthus
 */
public abstract class AbstractConversationHost implements ConversationHost {

    private final String name;
    private final String defaultConversationName;
    private final Map<String, String> playerConversations = new CaseInsensitiveMap<>();

    public AbstractConversationHost(String name, String defaultConversationName) {

        this.name = name;
        this.defaultConversationName = defaultConversationName;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public void setConversation(Player player, String conversation) {

        playerConversations.put(player.getName(), conversation);
    }

    @Override
    public String getConversation(Player player) {

        if (playerConversations.containsKey(player.getName())) {
            return playerConversations.get(player.getName());
        }
        return defaultConversationName;
    }
}

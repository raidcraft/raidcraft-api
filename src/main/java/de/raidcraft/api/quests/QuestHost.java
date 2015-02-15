package de.raidcraft.api.quests;

import de.raidcraft.api.conversations.ConversationHost;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface QuestHost extends ConversationHost {

    public String getId();

    public String getName();

    public String getType();

    public String getBasePath();

    public String getFriendlyName();

    public String getEndConversationName();

    public String getActiveConversationName();

    public void interact(Player player);

    public void spawn();

    public void despawn();
}

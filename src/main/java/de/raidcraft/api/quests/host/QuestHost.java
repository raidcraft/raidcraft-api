package de.raidcraft.api.quests.host;

import de.raidcraft.api.conversations.ConversationHost;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface QuestHost extends ConversationHost {

    String getId();

    String getName();

    String getType();

    String getBasePath();

    String getFriendlyName();

    void interact(Player player);

    boolean spawn();

    void despawn();
}

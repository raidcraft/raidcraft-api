package de.raidcraft.api.quests.host;

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

    public void interact(Player player);

    public boolean spawn();

    public void despawn();
}

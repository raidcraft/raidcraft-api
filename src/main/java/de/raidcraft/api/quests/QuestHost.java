package de.raidcraft.api.quests;

import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface QuestHost {

    public String getId();

    public String getName();

    public String getType();

    public String getBasePath();

    public String getFriendlyName();

    public void interact(Player player);

    public void spawn();

    public void despawn();
}

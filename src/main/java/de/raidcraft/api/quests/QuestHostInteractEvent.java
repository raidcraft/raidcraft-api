package de.raidcraft.api.quests;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
public class QuestHostInteractEvent extends Event {

    private final QuestHost host;
    private final Player player;

    public QuestHostInteractEvent(QuestHost host, Player player) {

        this.host = host;
        this.player = player;
    }

    public QuestHost getHost() {

        return host;
    }

    public Player getPlayer() {

        return player;
    }

    /*///////////////////////////////////////////////////
    //              Needed Bukkit Stuff
    ///////////////////////////////////////////////////*/

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}

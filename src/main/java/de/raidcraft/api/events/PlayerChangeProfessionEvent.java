package de.raidcraft.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
public class PlayerChangeProfessionEvent extends Event {

    private final Player player;
    private final String newProfession;
    private final int professionLevel;

    public PlayerChangeProfessionEvent(Player player, String newProfession, int professionLevel) {

        this.player = player;
        this.newProfession = newProfession;
        this.professionLevel = professionLevel;
    }

    public Player getPlayer() {

        return player;
    }

    public String getNewProfession() {

        return newProfession;
    }

    public int getProfessionLevel() {

        return professionLevel;
    }

    //<-- Handler -->//

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}

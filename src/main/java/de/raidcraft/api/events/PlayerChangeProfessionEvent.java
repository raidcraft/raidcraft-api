package de.raidcraft.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * @author Silthus
 */
public class PlayerChangeProfessionEvent extends PlayerEvent {

    private static final HandlerList HANDLER = new HandlerList();

    private final String newProfession;
    private final int professionLevel;

    public PlayerChangeProfessionEvent(Player player, String newProfession, int professionLevel) {

        super(player);
        this.newProfession = newProfession;
        this.professionLevel = professionLevel;
    }

    public static HandlerList getHandlerList() {

        return HANDLER;
    }

    public String getNewProfession() {

        return newProfession;
    }

    public int getProfessionLevel() {

        return professionLevel;
    }

    @Override
    public HandlerList getHandlers() {

        return HANDLER;
    }
}

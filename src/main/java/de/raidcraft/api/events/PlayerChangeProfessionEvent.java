package de.raidcraft.api.events;

import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class PlayerChangeProfessionEvent extends RCPlayerEvent {

    private final String newProfession;
    private final int professionLevel;

    public PlayerChangeProfessionEvent(Player player, String newProfession, int professionLevel) {

        super(player);
        this.newProfession = newProfession;
        this.professionLevel = professionLevel;
    }

    public String getNewProfession() {

        return newProfession;
    }

    public int getProfessionLevel() {

        return professionLevel;
    }
}

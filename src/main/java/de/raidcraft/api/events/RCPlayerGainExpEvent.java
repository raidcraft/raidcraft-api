package de.raidcraft.api.events;

import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class RCPlayerGainExpEvent extends RCPlayerEvent {

    private final int exp;

    public RCPlayerGainExpEvent(Player player, int exp) {

        super(player);
        this.exp = exp;
    }

    public int getGainedExp() {

        return exp;
    }
}

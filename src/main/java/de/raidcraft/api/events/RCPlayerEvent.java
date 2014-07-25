package de.raidcraft.api.events;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 *
 */
public class RCPlayerEvent extends RCEvent {

    @Getter(AccessLevel.PUBLIC)
    protected final Player player;

    public RCPlayerEvent(final Player who) {

        this.player = who;
    }

    public RCPlayerEvent(final Player who, final boolean isAsync) {

        super(isAsync);
        this.player = who;
    }
}

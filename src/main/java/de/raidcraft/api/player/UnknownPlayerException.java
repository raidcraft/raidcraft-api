package de.raidcraft.api.player;

import de.raidcraft.api.RaidCraftException;

/**
 * @author Silthus
 */
public class UnknownPlayerException extends RaidCraftException {

    public UnknownPlayerException(String message) {

        super(message);
    }
}

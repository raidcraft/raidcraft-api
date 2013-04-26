package de.raidcraft.api.items;

import de.raidcraft.api.RaidCraftException;

/**
 * @author Silthus
 */
public class DuplicateCustomItemException extends RaidCraftException {

    public DuplicateCustomItemException(String message) {

        super(message);
    }
}

package de.raidcraft.api.inventory;

import de.raidcraft.api.RaidCraftException;

/**
 * @author Silthus
 */
public class InvalidInventoryException extends RaidCraftException {

    public InvalidInventoryException(String message) {

        super(message);
    }
}

package de.raidcraft.api.flight.passenger;

import de.raidcraft.api.flight.flight.Flight;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public interface Passenger<T extends LivingEntity> {

    String getName();

    T getEntity();

    /**
     * Assigns the passenger a flight.
     *
     * @param flight to set
     */
    void setFlight(Flight flight);

    /**
     * Gets the active flight of the player. Can be null if player !isFlying().
     *
     * @return active flight or null if !isFlying()
     */
    Flight getFlight();

    /**
     * Checks if the passenger has an active flight.
     *
     * @return false if flight is null
     */
    boolean hasFlight();

    /**
     * Checks if the player is currently on a flight.
     * It does not check if the player is flying in the per minecraft sense.
     *
     * @return true if player is in an active flight
     */
    boolean isFlying();

    /**
     * Sends a message to the passenger if it is an entity that can receive messages.
     *
     * @param message to send
     */
    void sendMessage(String message);
}

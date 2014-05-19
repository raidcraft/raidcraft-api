package de.raidcraft.api.flight.passenger;

import de.raidcraft.api.flight.flight.Flight;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public interface Passenger<T extends LivingEntity> {

    public String getName();

    public T getEntity();

    /**
     * Assigns the passenger a flight.
     *
     * @param flight to set
     */
    public void setFlight(Flight flight);

    /**
     * Gets the active flight of the player. Can be null if player !isFlying().
     *
     * @return active flight or null if !isFlying()
     */
    public Flight getFlight();

    /**
     * Checks if the passenger has an active flight.
     *
     * @return false if flight is null
     */
    public boolean hasFlight();

    /**
     * Checks if the player is currently on a flight.
     * It does not check if the player is flying in the per minecraft sense.
     *
     * @return true if player is in an active flight
     */
    public boolean isFlying();
}

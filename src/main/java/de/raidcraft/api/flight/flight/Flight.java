package de.raidcraft.api.flight.flight;

import de.raidcraft.api.flight.aircraft.Aircraft;
import de.raidcraft.api.flight.passenger.Passenger;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;

/**
 * @author Silthus
 */
public interface Flight {

    /**
     * Gets the aircraft attached to this flight.
     * Can be a dragon or anything else in the future.
     *
     * @return aircraft mountable by a passenger
     */
    @Nullable
    Aircraft getAircraft();

    /**
     * Gets the flight path the aircraft will take.
     *
     * @return flight path
     */
    Path getPath();

    /**
     * Gets the interval in ticks at which the aircraft should move.
     *
     * @return interval in ticks (20 ticks = 1 second)
     */
    long getMoveInterval();

    /**
     * Checks if the flight is active and {@link de.raidcraft.api.flight.aircraft.Aircraft#isFlying()}
     *
     * @return true if flight is active
     */
    boolean isActive();

    /**
     * Gets the first waypoint of the path and therefor the flight.
     *
     * @return starting location of the flight
     */
    Location getStartLocation();

    /**
     * Gets the last waypoint of the path and therefor the flight.
     *
     * @return end location of the flight
     */
    Location getEndLocation();

    /**
     * Checks if the passenger list contains a passenger that matches the given entity.
     *
     * @param entity to match passenger list against
     *
     * @return true if passenger list contains this entity
     */
    boolean hasPassenger(LivingEntity entity);

    /**
     * Returns the current passenger of this aircraft
     *
     * @return passenger of the aircraft
     */
    Passenger getPassenger();

    /**
     * Sets the passenger of the aircraft.
     *
     * @param passenger to add to the aircraft
     */
    void setPassenger(Passenger<?> passenger);

    /**
     * Removes the given passenger from the aircraft. If a flight is in progress it will {@link Flight#abortFlight()} the flight.
     *
     * @return removed passenger or null if passenger could not be removed or wasnt on the aircraft
     */
    Passenger<?> removePassenger();

    void startDelayedFlight(int delay);

    /**
     * Starts the flight, mounting the passenger and flying to the first waypoint.
     */
    void startFlight();

    /**
     * Aborts the flight, unmounts the passenger and returns him to the startStage location of the flight.
     */
    void abortFlight();

    /**
     * Ends the flight gracefully, unmounting the passenger without returning him to the startStage.
     */
    void endFlight();

    /**
     * Checks if the flight path still has a next waypoint
     *
     * @return false if the path is at the end
     */
    boolean hasNextWaypoint();

    /**
     * Gets the next waypoint of the flight path.
     *
     * @return next waypoint
     */
    Waypoint nextWaypoint();

    /**
     * Gets the current last waypoint of the flight path.
     *
     * @return current waypoint of the path
     */
    Waypoint getCurrentWaypoint();
}

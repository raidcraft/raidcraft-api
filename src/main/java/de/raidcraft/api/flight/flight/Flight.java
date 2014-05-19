package de.raidcraft.api.flight.flight;

import de.raidcraft.api.flight.aircraft.Aircraft;
import de.raidcraft.api.flight.passenger.Passenger;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

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
    public Aircraft getAircraft();

    /**
     * Gets the flight path the aircraft will take.
     *
     * @return flight path
     */
    public Path getPath();

    /**
     * Gets the interval in ticks at which the aircraft should move.
     *
     * @return interval in ticks (20 ticks = 1 second)
     */
    public long getMoveInterval();

    /**
     * Checks if the flight is active and {@link de.raidcraft.api.flight.aircraft.Aircraft#isFlying()}
     *
     * @return true if flight is active
     */
    public boolean isActive();

    /**
     * Gets the first waypoint of the path and therefor the flight.
     *
     * @return starting location of the flight
     */
    public Location getFirstWaypoint();

    /**
     * Gets the last waypoint of the path and therefor the flight.
     *
     * @return end location of the flight
     */
    public Location getLastWaypoint();

    /**
     * Checks if the passenger list contains a passenger that matches the given entity.
     *
     * @param entity to match passenger list against
     *
     * @return true if passenger list contains this entity
     */
    public boolean hasPassenger(LivingEntity entity);

    /**
     * Returns the current passenger of this aircraft
     *
     * @return passenger of the aircraft
     */
    public Passenger getPassenger();

    /**
     * Sets the passenger of the aircraft.
     *
     * @param passenger to add to the aircraft
     */
    public void setPassenger(Passenger<?> passenger);

    /**
     * Removes the given passenger from the aircraft. If a flight is in progress it will {@link Flight#abortFlight()} the flight.
     *
     * @return removed passenger or null if passenger could not be removed or wasnt on the aircraft
     */
    public Passenger<?> removePassenger();

    /**
     * Starts the flight, mounting the passenger and flying to the first waypoint.
     */
    public void startFlight() throws de.raidcraft.api.flight.flight.FlightException;

    /**
     * Aborts the flight, unmounts the passenger and returns him to the start location of the flight.
     */
    public void abortFlight();

    /**
     * Ends the flight gracefully, unmounting the passenger without returning him to the start.
     */
    public void endFlight() throws de.raidcraft.api.flight.flight.FlightException;

    /**
     * Checks if the flight path still has a next waypoint
     *
     * @return false if the path is at the end
     */
    public boolean hasNextWaypoint();

    /**
     * Gets the next waypoint of the flight path.
     *
     * @return next waypoint
     */
    public Waypoint nextWaypoint();

    /**
     * Gets the current last waypoint of the flight path.
     *
     * @return current waypoint of the path
     */
    public Waypoint getCurrentWaypoint();
}

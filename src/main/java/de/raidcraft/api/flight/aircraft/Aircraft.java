package de.raidcraft.api.flight.aircraft;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.flight.flight.FlightException;
import de.raidcraft.api.flight.flight.Waypoint;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Silthus
 */
public interface Aircraft<T> {
    
    /**
     * Gets the Bukkit Entity associated with the aircraft.
     * 
     * @return bukkit entity or null if non bukkit entity
     */
    public Entity getBukkitEntity();

    /**
     * Gets entity that is the aircraft if it was spawned.
     *
     * @return null if {@link #spawn(org.bukkit.Location)} was not called
     */
    public T getEntity();

    /**
     * Checks if the aircraft reached the given waypoint.
     *
     * @param waypoint to reach
     *
     * @return true if aircraft is near the waypoint
     */
    public boolean hasReachedWaypoint(Waypoint waypoint);

    /**
     * Checks if the aircraft reached the given waypoint.
     *
     * @param waypoint to reach
     * @param radius to check around the waypoint
     *
     * @return true if aircraft is near the waypoint
     */
    public boolean hasReachedWaypoint(Waypoint waypoint, int radius);

    /**
     * Gets the current location of the aircraft.
     *
     * @return null if aircraft was not spawned
     */
    public Location getCurrentLocation();

    /**
     * Checks if the entity was spawned.
     *
     * @return true if entity exists and was spawned
     */
    public boolean isSpawned();

    /**
     * Moves the aircraft to the given waypoint
     *
     * @param flight that is moving the aircraft
     * @param waypoint to move to
     */
    public void move(Flight flight, Waypoint waypoint);

    /**
     * Stops the movement of the aircraft.
     */
    public void stopMoving();

    /**
     * Spawns the aircraft allowing it to take off and to accept passengers.
     *
     * @return spanwed aircraft
     */
    public T spawn(Location location);

    /**
     * Despawns the entity after landing or aborting the flight.
     */
    public void despawn();

    /**
     * Checks if the aircraft is flying and {@link #takeoff(de.raidcraft.api.flight.flight.Flight)}
     * was called and no {@link #land(de.raidcraft.api.flight.flight.Flight)} was done.
     *
     * @return true if aircraft is flying
     */
    public boolean isFlying();

    /**
     * Sets the aircraft as flying.
     * @param flying mode
     */
    public void setFlying(boolean flying);

    public BukkitTask getAircraftMoverTask();

    public void setAircraftMoverTask(BukkitTask aircraftMoverTask);

    /**
     * Will switch the aircraft into flying mode and strap on all the seatbelts for passengers on the aircraft.
     * Activates the flight mode which can abort user interaction with anything and so on.
     * If the aircraft {@link #isFlying()} it will not do anything
     * Please keep your seatbelt on if you are seated :)
     *
     * @param flight that triggered the takeoff
     */
    public default void takeoff(Flight flight) {

        if (!isFlying()) {
            try {
                setFlying(true);
                if (!isSpawned()) spawn(flight.getFirstWaypoint());
                mountPassenger(flight);
                move(flight, flight.getPath().getFirstWaypoint());
                // lets start the task that moves the aircraft around from waypoint to waypoint
                RaidCraftPlugin plugin = RaidCraft.getComponent(RaidCraftPlugin.class);
                setAircraftMoverTask(Bukkit.getScheduler().runTaskTimer(plugin,
                        new de.raidcraft.api.flight.aircraft.AircraftMoverTask(this, flight),
                        flight.getMoveInterval(),
                        flight.getMoveInterval()));
            } catch (FlightException ignored) {
            }
        }
    }

    /**
     * Will abort the flight if {@link #isFlying()}
     * and teleport all passengers to the {@link de.raidcraft.api.flight.flight.Flight#getFirstWaypoint()}
     *
     * @param flight that triggered the abort
     */
    public default void abortFlight(Flight flight) {

        if (isFlying()) {
            setFlying(false);
            stopMoving();
            unmountPassenger(flight);
            getAircraftMoverTask().cancel();
            if (isSpawned()) despawn();
        }
    }

    /**
     * Lands the aircraft safely on the ground removing restrictions from the passengers.
     * Will not do anything if the aircraft !{@link #isFlying()}
     *
     * @param flight that triggered the landing
     */
    public default void land(Flight flight) {

        if (isFlying()) {
            setFlying(false);
            stopMoving();
            unmountPassenger(flight);
            getAircraftMoverTask().cancel();
            if (isSpawned()) despawn();
        }
    }

    /**
     * Mounts all attached passengers onto the aircraft.
     *
     * @throws de.raidcraft.api.flight.flight.FlightException if entity was not spawned
     */
    public void mountPassenger(Flight flight) throws FlightException;

    /**
     * Unmounts all passengers from the aircraft.
     */
    public void unmountPassenger(Flight flight);
}
package de.raidcraft.api.flight.aircraft;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.flight.flight.Waypoint;
import de.raidcraft.util.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
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
    Entity getBukkitEntity();

    /**
     * Gets entity that is the aircraft if it was spawned.
     *
     * @return null if {@link #spawn(org.bukkit.Location)} was not called
     */
    T getEntity();

    /**
     * Loads the aircraft with the given config.
     *
     * @param config to load {@link Aircraft} with.
     */
    default void load(AircraftConfig config) {}

    /**
     * Checks if the aircraft reached the given waypoint.
     *
     * @param waypoint to reach
     *
     * @return true if aircraft is near the waypoint
     */
    boolean hasReachedWaypoint(Waypoint waypoint);

    /**
     * Checks if the aircraft reached the given waypoint.
     *
     * @param waypoint to reach
     * @param radius   to check around the waypoint
     *
     * @return true if aircraft is near the waypoint
     */
    boolean hasReachedWaypoint(Waypoint waypoint, int radius);

    /**
     * Gets the current location of the aircraft.
     *
     * @return null if aircraft was not spawned
     */
    Location getCurrentLocation();

    /**
     * Checks if the entity was spawned.
     *
     * @return true if entity exists and was spawned
     */
    boolean isSpawned();

    /**
     * Moves the aircraft to the given waypoint
     *
     * @param flight   that is moving the aircraft
     * @param waypoint to move to
     */
    void move(Flight flight, Waypoint waypoint);

    /**
     * Starts the navigation and sets up all important variables.
     */
    void startNavigation(Flight flight);

    /**
     * Stops the movement of the aircraft.
     */
    void stopNavigation(Flight flight);

    /**
     * Spawns the aircraft allowing it to take off and to accept passengers.
     *
     * @return spanwed aircraft
     */
    T spawn(Location location);

    /**
     * Despawns the entity after landing or aborting the flight.
     */
    void despawn();

    /**
     * Checks if the aircraft is flying and {@link #takeoff(de.raidcraft.api.flight.flight.Flight)}
     * was called and no {@link #land(de.raidcraft.api.flight.flight.Flight)} was done.
     *
     * @return true if aircraft is flying
     */
    boolean isFlying();

    /**
     * Sets the aircraft as flying.
     *
     * @param flying mode
     */
    void setFlying(boolean flying);

    BukkitTask getAircraftMoverTask();

    void setAircraftMoverTask(BukkitTask aircraftMoverTask);

    /**
     * Will switch the aircraft into flying mode and strap on all the seatbelts for passengers on the aircraft.
     * Activates the flight mode which can abort user interaction with anything and so on.
     * If the aircraft {@link #isFlying()} it will not do anything
     * Please keep your seatbelt on if you are seated :)
     *
     * @param flight that triggered the takeoff
     */
    default void takeoff(final Flight flight) {

        if (!isFlying()) {
            setFlying(true);
            Location start = flight.getStartLocation();
            Location end = flight.getEndLocation();
            start.setPitch(0);
            start.setYaw(BukkitUtil.lookAtIgnoreY(start.getX(), start.getZ(),
                    end.getX(), end.getZ()));
            spawn(flight.getStartLocation());
            final RaidCraftPlugin plugin = RaidCraft.getComponent(RaidCraftPlugin.class);
            new BukkitRunnable() {
                @Override
                public void run() {
                    // wait until dragon is spawned
                    if (!isSpawned()) {
                        plugin.getLogger().info("Dragon not spawned");
                        return;
                    }
                    cancel();
                    // startStage flight
                    mountPassenger(flight);
                    startNavigation(flight);
                    move(flight, flight.getPath().getFirstWaypoint());
                    if (flight.getMoveInterval() > 0) {
                        // lets startStage the task that moves the aircraft around from waypoint to waypoint
                        setAircraftMoverTask(Bukkit.getScheduler().runTaskTimer(plugin,
                                new de.raidcraft.api.flight.aircraft.AircraftMoverTask(Aircraft.this, flight),
                                -1, flight.getMoveInterval()));
                    } else {
                        plugin.getLogger().info("Move Interval from flight to fast");
                    }
                }
            }.runTaskTimer(plugin, -1, 1);
        }
    }

    /**
     * Will abort the flight if {@link #isFlying()}
     * and teleport all passengers to the {@link de.raidcraft.api.flight.flight.Flight#getStartLocation()}
     *
     * @param flight that triggered the abort
     */
    default void abortFlight(Flight flight) {

        if (isFlying()) {
            setFlying(false);
            stopNavigation(flight);
            unmountPassenger(flight);
            if (getAircraftMoverTask() != null) getAircraftMoverTask().cancel();
            if (isSpawned()) despawn();
        }
    }

    /**
     * Lands the aircraft safely on the ground removing restrictions from the passengers.
     * Will not do anything if the aircraft !{@link #isFlying()}
     *
     * @param flight that triggered the landing
     */
    default void land(Flight flight) {

        if (isFlying()) {
            setFlying(false);
            stopNavigation(flight);
            unmountPassenger(flight);
            getAircraftMoverTask().cancel();
            if (isSpawned()) despawn();
        }
    }

    /**
     * Mounts all attached passengers onto the aircraft.
     */
    void mountPassenger(Flight flight);

    /**
     * Unmounts all passengers from the aircraft.
     */
    void unmountPassenger(Flight flight);
}
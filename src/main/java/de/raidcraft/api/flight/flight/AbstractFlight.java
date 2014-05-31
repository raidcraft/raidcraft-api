package de.raidcraft.api.flight.flight;

import de.raidcraft.api.flight.aircraft.Aircraft;
import de.raidcraft.api.flight.passenger.Passenger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public abstract class AbstractFlight implements de.raidcraft.api.flight.flight.Flight {

    private final Aircraft<?> aircraft;
    private final Path path;
    private final Location startLocation;
    private final Location endLocation;
    private Passenger<?> passenger;
    private int currentIndex = 0;

    public AbstractFlight(Aircraft<?> aircraft, Path path, Location startLocation, Location endLocation) {

        this.aircraft = aircraft;
        this.path = path;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }

    @Override
    public Aircraft<?> getAircraft() {

        return aircraft;
    }

    @Override
    public Path getPath() {

        return path;
    }

    @Override
    public Location getFirstWaypoint() {

        return startLocation;
    }

    @Override
    public Location getLastWaypoint() {

        return endLocation;
    }

    @Override
    public boolean hasPassenger(LivingEntity entity) {

        return getPassenger() != null && getPassenger().getEntity().equals(entity);
    }

    @Override
    public Passenger<?> getPassenger() {

        return passenger;
    }

    @Override
    public Passenger<?> removePassenger() {

        Passenger<?> passenger = this.passenger;
        this.passenger = null;
        return passenger;
    }

    @Override
    public void setPassenger(Passenger<?> passenger) {

        this.passenger = passenger;
    }

    @Override
    public boolean isActive() {

        return getAircraft().isFlying();
    }

    @Override
    public boolean hasNextWaypoint() {

        return currentIndex + 1 < getPath().getWaypoints().size();
    }

    @Override
    public Waypoint nextWaypoint() {

        return getPath().getWaypoints().get(++currentIndex);
    }

    @Override
    public Waypoint getCurrentWaypoint() {

        return getPath().getWaypoints().get(currentIndex);
    }

    @Override
    public final void startFlight() {

        if (isActive()) return;
        try {
            onStartFlight();
            getAircraft().takeoff(this);
            getPassenger().setFlight(this);
        } catch (FlightException e) {
            getPassenger().sendMessage(ChatColor.RED + e.getMessage());
            e.printStackTrace();
        }
    }

    public abstract void onStartFlight() throws FlightException;

    @Override
    public final void abortFlight() {

        if (!isActive()) return;
        try {
            onAbortFlight();
            getAircraft().abortFlight(this);
            getPassenger().getEntity().teleport(getFirstWaypoint());
        } catch (FlightException e) {
            getPassenger().sendMessage(ChatColor.RED + e.getMessage());
            e.printStackTrace();
        }
    }

    public abstract void onAbortFlight() throws FlightException;

    @Override
    public final void endFlight() {

        if (!isActive()) return;
        try {
            onEndFlight();
            getAircraft().land(this);
        } catch (FlightException e) {
            getPassenger().sendMessage(ChatColor.RED + e.getMessage());
            e.printStackTrace();
        }
    }

    public abstract void onEndFlight() throws FlightException;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractFlight)) return false;

        AbstractFlight that = (AbstractFlight) o;

        if (currentIndex != that.currentIndex) return false;
        if (!aircraft.equals(that.aircraft)) return false;
        if (!path.equals(that.path)) return false;

        return true;
    }

    @Override
    public int hashCode() {

        int result = aircraft.hashCode();
        result = 31 * result + path.hashCode();
        result = 31 * result + currentIndex;
        return result;
    }
}

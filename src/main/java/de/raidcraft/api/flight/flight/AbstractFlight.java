package de.raidcraft.api.flight.flight;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.flight.aircraft.Aircraft;
import de.raidcraft.api.flight.passenger.Passenger;
import org.bukkit.Bukkit;
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
    public Location getStartLocation() {

        return startLocation;
    }

    @Override
    public Location getEndLocation() {

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

        return getAircraft() != null && getAircraft().isFlying();
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

        if (currentIndex < getPath().getWaypoints().size()) {
            return getPath().getWaypoints().get(currentIndex);
        }
        return getPath().getLastWaypoint();
    }

    @Override
    public void startDelayedFlight(int delay) {
        if (delay < 1) {
            startFlight();
            return;
        }

        Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(RaidCraftPlugin.class), this::startFlight, delay);
    }

    @Override
    public synchronized void startFlight() {

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
    public synchronized void abortFlight() {
        try {
            onAbortFlight();
            if(getAircraft() != null) getAircraft().abortFlight(this);
            Location improvedLocation = getStartLocation().clone();
            improvedLocation.add(0,4,0); // add some high to prevent glitching
            getPassenger().getEntity().teleport(improvedLocation);
        } catch (FlightException e) {
            getPassenger().sendMessage(ChatColor.RED + e.getMessage());
            e.printStackTrace();
        }
    }

    public abstract void onAbortFlight() throws FlightException;

    @Override
    public void endFlight() {
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
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {

        int result = aircraft.hashCode();
        result = 31 * result + path.hashCode();
        result = 31 * result + currentIndex;
        return result;
    }
}

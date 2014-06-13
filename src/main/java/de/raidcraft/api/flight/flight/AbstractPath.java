package de.raidcraft.api.flight.flight;

import de.raidcraft.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Silthus
 */
public abstract class AbstractPath implements Path {

    private final List<Waypoint> waypoints = new LinkedList<>();
    private boolean showingWaypoints = false;
    private Location start;
    private Location end;

    public AbstractPath(Location start, Location end) {

        this.start = start;
        this.end = end;
    }

    public AbstractPath() {

    }

    public Location getStartLocation() {

        return start;
    }

    public Location getEndLocation() {

        return end;
    }

    public void setStartLocation(Location start) {

        this.start = start;
    }

    public void setEndLocation(Location end) {

        this.end = end;
    }

    @Override
    public void showWaypoints() {

        showingWaypoints = true;
        getWaypoints().forEach(this::spawnEnderCrystal);
    }

    @Override
    public void hideWaypoints() {

        showingWaypoints = false;
        getWaypoints().forEach(this::removeEnderCrystal);
    }

    @Override
    public boolean isShowingWaypoints() {

        return showingWaypoints;
    }

    @Override
    public boolean containsWaypoint(Waypoint waypoint) {

        return waypoints.contains(waypoint);
    }

    @Override
    public boolean containsWaypoint(Location location) {

        return containsWaypoint(new Waypoint(location));
    }

    @Override
    public Waypoint getFirstWaypoint() {

        if (!waypoints.isEmpty()) {
            return waypoints.get(0);
        }
        return null;
    }

    @Override
    public Waypoint getLastWaypoint() {

        if (!waypoints.isEmpty()) {
            return waypoints.get(waypoints.size() - 1);
        }
        return null;
    }

    protected List<Waypoint> clearWaypoints() {

        ArrayList<Waypoint> waypoints = new ArrayList<>(this.waypoints);
        this.waypoints.clear();
        return waypoints;
    }

    @Override
    public int getWaypointAmount() {

        return waypoints.size();
    }

    @Override
    public Waypoint getWaypoint(int index) {

        return waypoints.get(index);
    }

    @Override
    public Waypoint removeWaypoint(int index) {

        Waypoint waypoint = waypoints.remove(index);
        removeEnderCrystal(waypoint);
        return waypoint;
    }

    @Override
    public Waypoint removeWaypoint(Waypoint waypoint) {

        if (waypoints.remove(waypoint)) {
            removeEnderCrystal(waypoint);
            return waypoint;
        }
        return null;
    }

    @Override
    public void addWaypoint(Waypoint waypoint) {

        waypoints.add(waypoint);
        spawnEnderCrystal(waypoint);
    }

    @Override
    public void addWaypoint(int index, Waypoint waypoint) {

        if (index > waypoints.size()) {
            addWaypoint(waypoint);
        } else {
            waypoints.add(index, waypoint);
            spawnEnderCrystal(waypoint);
        }
    }

    @Override
    public Waypoint setWaypoint(int index, Waypoint waypoint) {

        if (index > waypoints.size()) {
            waypoints.add(waypoint);
            spawnEnderCrystal(waypoint);
            return waypoint;
        } else {
            Waypoint oldWaypoint = waypoints.set(index, waypoint);
            removeEnderCrystal(oldWaypoint);
            spawnEnderCrystal(waypoint);
            return oldWaypoint;
        }
    }

    @Override
    public List<Waypoint> getWaypoints() {

        return waypoints;
    }

    private void spawnEnderCrystal(Waypoint waypoint) {

        if (!isShowingWaypoints()) return;
        waypoint.getLocation().getWorld().spawnEntity(waypoint.getLocation(), EntityType.ENDER_CRYSTAL);
    }

    private void removeEnderCrystal(Waypoint waypoint) {

        if (waypoint == null) return;
        for (Entity entity : LocationUtil.getNearbyEntities(waypoint.getLocation(), 1)) {
            if (entity instanceof EnderCrystal) {
                entity.remove();
            }
        }
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractPath)) return false;

        AbstractPath that = (AbstractPath) o;

        if (!end.equals(that.end)) return false;
        if (!start.equals(that.start)) return false;
        if (!waypoints.equals(that.waypoints)) return false;

        return true;
    }

    @Override
    public int hashCode() {

        int result = waypoints.hashCode();
        result = 31 * result + start.hashCode();
        result = 31 * result + end.hashCode();
        return result;
    }
}

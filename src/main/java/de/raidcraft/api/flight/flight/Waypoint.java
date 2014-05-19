package de.raidcraft.api.flight.flight;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author Silthus
 */
public class Waypoint {

    private final Location location;

    public Waypoint(Location location) {

        this.location = location;
    }

    public Waypoint(World world, double x, double y, double z) {

        this(new Location(world, x, y, z));
    }

    public Location getLocation() {

        return location;
    }

    public World getWorld() {

        return location.getWorld();
    }

    public double getX() {

        return location.getX();
    }

    public double getY() {

        return location.getY();
    }

    public double getZ() {

        return location.getZ();
    }

    public void setX(double x) {

        location.setX(x);
    }

    public void setY(double y) {

        location.setY(y);
    }

    public void setZ(double z) {

        location.setZ(z);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {

        super.clone();
        return new Waypoint(getLocation());
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof Waypoint)) return false;

        Waypoint waypoint = (Waypoint) o;

        return !(location != null ? !location.equals(waypoint.location) : waypoint.location != null);
    }

    @Override
    public int hashCode() {

        return location != null ? location.hashCode() : 0;
    }
}

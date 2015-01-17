package de.raidcraft.api.flight.flight;

import org.bukkit.Location;

import java.util.List;

/**
 * @author Silthus
 */
public interface Path {

    /**
     * Shows all waypoints in the path as blocks placing the defined material.
     */
    public void showWaypoints();

    /**
     * Hides all waypoints in this path, placing air the waypoint location.
     */
    public void hideWaypoints();

    /**
     * Checks if the path is showing its waypoints.
     *
     * @return true if waypoints are shown
     */
    public boolean isShowingWaypoints();

    /**
     * Checks if the path contains the given waypoint.
     *
     * @param waypoint to check for
     *
     * @return true if path contains waypoint
     */
    public boolean containsWaypoint(Waypoint waypoint);

    /**
     * Checks if the path contains the given location as a waypoint.
     *
     * @param location to check for
     *
     * @return true if path has a waypoint with the location
     */
    public boolean containsWaypoint(Location location);

    /**
     * Gets the starting waypoint in this path.
     *
     * @return starting waypoint
     */
    public Waypoint getFirstWaypoint();

    /**
     * Gets the last waypoint in this path.
     *
     * @return last waypoint
     */
    public Waypoint getLastWaypoint();

    /**
     * Gets the amount of waypoints in this path.
     *
     * @return waypoint amount
     */
    public int getWaypointAmount();

    /**
     * Gets the waypoint at the specified index.
     *
     * @param index to get waypoint for
     *
     * @return waypoint at index or throws {@link java.lang.IndexOutOfBoundsException}
     */
    public Waypoint getWaypoint(int index);

    /**
     * Removes the waypoint at the given index of the path.
     *
     * @param index to remove waypoint at
     *
     * @return null if index is invalid or no waypoint was found
     */
    public Waypoint removeWaypoint(int index);

    /**
     * Removes all matching waypoints from the path.
     *
     * @param waypoint to remove
     *
     * @return null if no waypoint was found in the path
     */
    public Waypoint removeWaypoint(Waypoint waypoint);

    /**
     * Adds a waypoint to the end of the path.
     *
     * @param waypoint to add
     */
    public void addWaypoint(Waypoint waypoint);

    /**
     * Adds a waypoint at the given position of the path.
     * Will add the waypoint at the end of the list of index greater than size.
     * See {@link java.util.List#add(int, Object)}
     *
     * @param index    to insert waypoint at
     * @param waypoint to add
     */
    public void addWaypoint(int index, Waypoint waypoint);

    /**
     * Sets the waypoint at the given position overriding the existing waypoint.
     * Will add the waypoint at the end of the list of index greather than size.
     * See {@link java.util.List#set(int, Object)}
     *
     * @param index    to set waypoint at
     * @param waypoint to set
     *
     * @return the previous waypoint if replaced or the new one if added at the end
     */
    public Waypoint setWaypoint(int index, Waypoint waypoint);

    /**
     * Gets a list of all the waypoints in the path.
     *
     * @return list of all waypoints
     */
    public List<Waypoint> getWaypoints();

    /**
     * Calculates the waypoints for this path.
     * This is implemented by the different path types and may do nothing if the path is static.
     */
    public void calculate();
}

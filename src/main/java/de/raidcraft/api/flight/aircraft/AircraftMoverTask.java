package de.raidcraft.api.flight.aircraft;

import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.flight.flight.Waypoint;

/**
 * @author Silthus
 */
public class AircraftMoverTask implements Runnable {

    private final Aircraft aircraft;
    private final Flight flight;
    private final Waypoint lastWaypoint;
    private boolean landing = false;

    protected AircraftMoverTask(Aircraft aircraft, Flight flight) {

        this.aircraft = aircraft;
        this.flight = flight;
        this.lastWaypoint = new Waypoint(flight.getEndLocation());
    }

    @Override
    public void run() {

        if (!landing && !aircraft.hasReachedWaypoint(flight.getCurrentWaypoint())) {
            return;
        }
        if (!aircraft.isSpawned() && aircraft.isFlying()) {
            flight.abortFlight();
            return;
        }
        if (landing || !flight.hasNextWaypoint()) {
            if (!landing) {
                landing = true;
                aircraft.move(flight, lastWaypoint);
            } else if (aircraft.hasReachedWaypoint(lastWaypoint, 1)) {
                flight.endFlight();
            }
        } else {
            aircraft.move(flight, flight.nextWaypoint());
        }
    }
}

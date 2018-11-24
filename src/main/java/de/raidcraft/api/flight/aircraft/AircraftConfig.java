package de.raidcraft.api.flight.aircraft;

import lombok.Data;

@Data
public class AircraftConfig {

    private double speedX = 0.5;
    private double speedY = 0.5;
    private double speedZ = 0.5;
    private int waypointRadius = 3;
    private float playerPitch = 30f;
}

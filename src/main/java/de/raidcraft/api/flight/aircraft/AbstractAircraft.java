package de.raidcraft.api.flight.aircraft;

import lombok.Data;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Silthus
 */
@Data
public abstract class AbstractAircraft<T> implements Aircraft<T> {

    private boolean flying;
    private BukkitTask aircraftMoverTask;

    @Override
    public boolean isFlying() {

        return flying;
    }
}

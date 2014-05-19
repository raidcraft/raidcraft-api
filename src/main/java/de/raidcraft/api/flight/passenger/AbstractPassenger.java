package de.raidcraft.api.flight.passenger;

import de.raidcraft.api.flight.flight.Flight;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public abstract class AbstractPassenger<T extends LivingEntity> implements de.raidcraft.api.flight.passenger.Passenger<T> {

    private final T entity;
    private Flight flight;

    public AbstractPassenger(T entity) {

        this.entity = entity;
    }

    @Override
    public String getName() {

        if (getEntity() instanceof Player) {
            return ((Player) getEntity()).getName();
        }
        return getEntity().getCustomName();
    }

    @Override
    public T getEntity() {

        return entity;
    }

    @Override
    public void setFlight(Flight flight) {

        this.flight = flight;
    }

    @Override
    public Flight getFlight() {

        return flight;
    }

    @Override
    public boolean hasFlight() {

        return getFlight() != null;
    }

    @Override
    public boolean isFlying() {

        return getFlight() != null && getFlight().isActive();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractPassenger)) return false;

        AbstractPassenger that = (AbstractPassenger) o;

        if (!entity.equals(that.entity)) return false;
        if (!flight.equals(that.flight)) return false;

        return true;
    }

    @Override
    public int hashCode() {

        int result = entity.hashCode();
        result = 31 * result + flight.hashCode();
        return result;
    }
}

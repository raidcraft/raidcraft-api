package de.raidcraft.api.random;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

/**
 * This class holds a single RDS value.
 * It's a generic class to allow the developer to add any type to a RDSTable.
 * T can of course be either a value type or a reference type, so it's possible,
 * to add RDSValue objects that contain a reference type, too.
 * @param <T> The type of the value
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GenericRDSValue<T> extends GenericRDSObject implements RDSValue<T> {

    private Optional<T> value;

    public GenericRDSValue(T value, double probability) {

        this(value, probability, true, false, false);
    }

    public GenericRDSValue(T value, double probability, boolean enabled, boolean always, boolean unique) {

        super(probability, enabled, always, unique);
        this.value = Optional.ofNullable(value);
    }

    @Override
    public Optional<T> getValue() {

        return this.value;
    }

    @Override
    public void setValue(T value) {

        this.value = Optional.ofNullable(value);
    }
}

package de.raidcraft.api.random;

import java.util.Optional;

/**
 * Generic template for classes that return only one value, which will be good enough in most use cases.
 *
 * @param <T> The type of the value of this object
 */
public interface RDSValue<T> extends RDSObject {

    /**
     * Gets the optional value of this object.
     *
     * @return optional value
     */
    Optional<T> getValue();

    /**
     * Sets the value of the object.
     *
     * @param value to set
     */
    void setValue(T value);
}

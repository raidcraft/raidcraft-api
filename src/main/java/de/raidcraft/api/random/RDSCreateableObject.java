package de.raidcraft.api.random;

import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * This class is a special derived version of an RDSObject.
 * It implements the IRDSObjectCreator interface, which can be used to create custom instances of classes
 * when they are hit by the random engine.
 * The RDSTable class checks for this interface before a result is added to the result set.
 * If it is implemented, this object's CreateInstance method is called, and with this tweak it is possible
 * to enter completely new instances into the result set at the moment they are hit.
 */
public class RDSCreateableObject extends GenericRDSObject implements RDSObjectCreator {

    /**
     * Creates an instance of the object where this method is implemented in.
     * Only paramaterless constructors are supported in the base implementation.
     * Override (without calling base.CreateInstance()) to instanciate more complex constructors.
     *
     * @return A new instance of an object of the type where this method is implemented
     */
    @Override
    public RDSObject createInstance(ConfigurationSection config) {

        try {
            Constructor<? extends RDSCreateableObject> constructor = getClass().getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}

package de.raidcraft.api.random;

/**
 * This interface holds a method that creates an instance of an object where it is implemented.
 * If an object gets hit by RDS, it checks whether it is an ORDSObjectCreator. If yes, the result
 * of .CreateInstance() is added to the result; if not, the object itself is returned.
 */
public interface RDSObjectCreator {

    /**
     * Creates an instance of the object where this method is implemented in.
     * Only paramaterless constructors are supported in the base implementation.
     * Override (without calling base.CreateInstance()) to instanciate more complex constructors.
     *
     * @return A new instance of an object of the type where this method is implemented
     */
    RDSObject createInstance();
}

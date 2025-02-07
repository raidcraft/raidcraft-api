package de.raidcraft.api.requirement;

import java.util.List;

/**
 * @deprecated see {@link de.raidcraft.api.action.requirement.Requirement} API
 */
@Deprecated
public interface RequirementResolver<T> {

    T getObject();

    /**
     * Gets a list of all attached requirements.
     *
     * @return attached requirements
     */
    List<Requirement<T>> getRequirements();

    /**
     * Checks if the resolver is meeting all attached requirements.
     *
     * @return true if all requirements are met
     */
    boolean isMeetingAllRequirements(T object);

    /**
     * Gets a reason why this resolver cannot be unlocked.
     *
     * @return reason why unlock is not possible
     */
    String getResolveReason(T object);
}

package de.raidcraft.api.requirement;

import java.util.List;

/**
 * @author Silthus
 */
public interface RequirementResolver<T> {

    public T getObject();

    /**
     * Gets a list of all attached requirements.
     *
     * @return attached requirements
     */
    public List<Requirement> getRequirements();

    /**
     * Checks if the resolver is meeting all attached requirements.
     *
     * @return true if all requirements are met
     */
    public boolean isMeetingAllRequirements();

    /**
     * Gets a reason why this resolver cannot be unlocked.
     *
     * @return reason why unlock is not possible
     */
    public String getResolveReason();
}

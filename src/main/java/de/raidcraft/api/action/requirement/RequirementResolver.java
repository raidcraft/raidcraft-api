package de.raidcraft.api.action.requirement;

import java.util.List;

/**
 * @author mdoering
 */
public interface RequirementResolver<T> {

    public List<Requirement<T>> getRequirements();

    public boolean isMeetingAllRequirements(T entity);

    public String getResolveReason(T entity);
}

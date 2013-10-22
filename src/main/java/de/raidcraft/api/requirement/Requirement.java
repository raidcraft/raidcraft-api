package de.raidcraft.api.requirement;

/**
 * @author Silthus
 */
public interface Requirement<T> {

    public String getName();

    public String getDescription();

    public RequirementResolver<T> getResolver();

    public boolean isMet(T object);

    public String getShortReason();

    public String getLongReason();
}

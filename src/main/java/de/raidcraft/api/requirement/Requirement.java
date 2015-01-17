package de.raidcraft.api.requirement;

/**
 * @deprecated see {@link de.raidcraft.api.action.requirement.Requirement} API
 */
@Deprecated
public interface Requirement<T> {

    public String getName();

    public String getDescription();

    public RequirementResolver<T> getResolver();

    public boolean isMet(T object);

    public String getShortReason();

    public String getLongReason();
}

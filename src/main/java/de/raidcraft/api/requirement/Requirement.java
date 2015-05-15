package de.raidcraft.api.requirement;

/**
 * @deprecated see {@link de.raidcraft.api.action.requirement.Requirement} API
 */
@Deprecated
public interface Requirement<T> {

    String getName();

    String getDescription();

    RequirementResolver<T> getResolver();

    boolean isMet(T object);

    String getShortReason();

    String getLongReason();
}

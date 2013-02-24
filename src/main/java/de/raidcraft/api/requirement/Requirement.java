package de.raidcraft.api.requirement;

/**
 * @author Silthus
 */
public interface Requirement<T> {

    public String getName();

    public T getResolver();

    public boolean isMet();

    public String getShortReason();

    public String getLongReason();
}

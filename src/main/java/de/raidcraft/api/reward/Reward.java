package de.raidcraft.api.reward;

/**
 * @author Philip Urban
 */
public interface Reward<T> {

    public String getName();

    public String getDescription();

    public void reward(T object);
}

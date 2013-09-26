package de.raidcraft.api.reward;

/**
 * @author Philip Urban
 */
public interface Reward<T> {

    public String getName();

    public void reward(T object);
}

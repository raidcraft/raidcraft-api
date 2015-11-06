package de.raidcraft.api.reward;

/**
 * @author Philip Urban
 */
@Deprecated
public interface Reward<T> {

    String getName();

    String getDescription();

    void reward(T object);
}

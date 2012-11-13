package de.raidcraft.api.player;

/**
 * The Interface is used to tag Components that are attached to the player
 * and interact with him.
 *
 * All classes that implement this interface need to have a private constructor
 * with a single {@link RCPlayer} parameter.
 *
 * Classes that implement this interface should be designed like a player object.
 *
 * @author Silthus
 */
public interface PlayerComponent {

	public RCPlayer getPlayer();
}

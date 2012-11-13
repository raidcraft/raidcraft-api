package de.raidcraft.api.player;

import com.sk89q.worldedit.WorldVector;

/**
 * @author Silthus
 */
public interface RCPlayer {

	public <T extends PlayerComponent> T getComponent(Class<T> clazz);

	public String getUserName();

	public String getDisplayName();

	public boolean hasPermission(String permission);

	public String getWorld();

	public boolean isInWorld(String world);

	public void sendMessage(String... messages);

	public boolean isOnline();

	public WorldVector getLocation();

	public void teleport(WorldVector vector);

    public RCPlayer getTargetPlayer();
}

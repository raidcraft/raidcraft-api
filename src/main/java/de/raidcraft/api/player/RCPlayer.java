package de.raidcraft.api.player;

import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.WorldVector;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public interface RCPlayer {

	public <T extends PlayerComponent> T getComponent(Class<T> clazz);

	public String getUserName();

	public String getDisplayName();

    public boolean isOp();

	public boolean hasPermission(String permission);

    public void addMoney(double amount);

    public void removeMoney(double amount);

    public boolean hasEnoughMoney(double cost);

	public String getWorld();

	public boolean isInWorld(String world);

	public void sendMessage(String... messages);

	public boolean isOnline();

	public WorldVector getLocation();

	public void teleport(WorldVector vector);

    public RCPlayer getTargetPlayer();

    public LivingEntity getTarget();

    public BlockWorldVector getTargetBlock();
}

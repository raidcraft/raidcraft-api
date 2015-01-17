package de.raidcraft.api.player;

import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.WorldVector;
import de.raidcraft.api.InvalidTargetException;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Silthus
 */
public interface RCPlayer {

    public <T extends PlayerComponent> T getComponent(Class<T> clazz);

    public String getUserName();

    public String getDisplayName();

    public Player getBukkitPlayer();

    public boolean isOp();

    public boolean hasPermission(String permission);

    public String getWorld();

    public boolean isInWorld(String world);

    public void sendMessage(String... messages);

    public boolean isOnline();

    public WorldVector getLocation();

    public void teleport(WorldVector vector);

    public RCPlayer getTargetPlayer() throws InvalidTargetException;

    public LivingEntity getTarget() throws InvalidTargetException;

    public List<LivingEntity> getNearbyEntities(int radius);

    public BlockWorldVector getTargetBlock();

    public int getItemInHand();

    public boolean hasMoved(Location location);

    public void destroy();
}

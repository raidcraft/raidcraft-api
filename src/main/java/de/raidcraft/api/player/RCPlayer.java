package de.raidcraft.api.player;

import de.raidcraft.api.InvalidTargetException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Silthus
 */
public interface RCPlayer {

    <T extends PlayerComponent> T getComponent(Class<T> clazz);

    String getUserName();

    String getDisplayName();

    Player getBukkitPlayer();

    boolean isOp();

    boolean hasPermission(String permission);

    String getWorld();

    boolean isInWorld(String world);

    void sendMessage(String... messages);

    boolean isOnline();

    RCPlayer getTargetPlayer() throws InvalidTargetException;

    LivingEntity getTarget() throws InvalidTargetException;

    List<LivingEntity> getNearbyEntities(int radius);

    ItemStack getItemInHand();

    boolean hasMoved(Location location);

    void destroy();
}

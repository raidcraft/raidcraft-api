package de.raidcraft.api.bukkit;

import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.WorldVector;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.api.player.AbstractPlayer;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.util.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashSet;
import java.util.List;

/**
 * @author Silthus
 */
public class BukkitPlayer extends AbstractPlayer {

    private Player player;
    private int lastX;
    private int lastY;
    private int lastZ;

    public BukkitPlayer(Player player) {

        super(player.getName());
        this.player = player;
    }

    public BukkitPlayer(String username) {

        super(username);
        this.player = Bukkit.getPlayer(getUserName());
    }

    @Override
    public Player getBukkitPlayer() {

        return player;
    }

    @Override
    public String getWorld() {

        if (isOnline()) {
            return player.getWorld().getName();
        }
        return "";
    }

    @Override
    public void sendMessage(String... messages) {

        if (isOnline()) {
            for (String message : messages) {
                player.sendMessage(ChatColor.YELLOW + message);
            }
        }
    }

    @Override
    public boolean isOp() {

        return player.isOnline() && player.isOp();
    }

    @Override
    public boolean hasPermission(String permission) {

        if (isOnline()) {
            return player.hasPermission(permission);
        } else {
            // TODO: maybe not hard code this
            return true;
//            return RaidCraft.getPermissions().playerHas("world", getUserName(), permission);
        }
    }

    @Override
    public boolean isOnline() {

        if (player == null) {
            player = Bukkit.getPlayer(getUserName());
        }
        return player != null && player.isOnline();
    }

    @Override
    public WorldVector getLocation() {

        if (isOnline()) {
            return BukkitUtil.toWorldVector(player.getLocation());
        }
        return null;
    }

    @Override
    public void teleport(WorldVector vector) {

        player.teleport(BukkitUtil.getLocation(vector), PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Override
    public RCPlayer getTargetPlayer() throws InvalidTargetException {

        Player target = BukkitUtil.getTarget(player, player.getWorld().getPlayers());
        if (target == null) {
            throw new InvalidTargetException("Du hast kein Ziel im Sichtfeld!");
        }
        return RaidCraft.getPlayer(target);
    }

    @Override
    public BlockWorldVector getTargetBlock() {

        return BukkitUtil.toBlockWorldVector(player.getTargetBlock(new HashSet<Material>(), 100));
    }

    @Override
    public LivingEntity getTarget() throws InvalidTargetException {

        LivingEntity target = BukkitUtil.getTargetEntity(player, LivingEntity.class);
        if (target == null) {
            throw new InvalidTargetException("Du hast kein Ziel im Sichtfeld!");
        }
        return target;
    }

    @Override
    public List<LivingEntity> getNearbyEntities(int radius) {

        return BukkitUtil.getNearbyEntities(getBukkitPlayer(), radius);
    }

    @Override
    public int getItemInHand() {

        return player.getItemInHand().getTypeId();
    }

    @Override
    public boolean hasMoved(Location location) {

        boolean moved = location.getBlockX() != lastX || location.getBlockY() != lastY || location.getBlockZ() != lastZ;
        if (moved) {
            lastX = location.getBlockX();
            lastY = location.getBlockY();
            lastZ = location.getBlockZ();
        }
        return moved;
    }

    @Override
    public void destroy() {

        this.player = null;
    }
}

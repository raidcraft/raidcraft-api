package de.raidcraft.api.bukkit;

import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.WorldVector;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.player.AbstractPlayer;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.util.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * @author Silthus
 */
public class BukkitPlayer extends AbstractPlayer {

	private final Player player;

	public BukkitPlayer(Player player) {

		super(player.getName());
		this.player = player;
	}

	public BukkitPlayer(String username) {

		super(username);
		this.player = Bukkit.getPlayer(getUserName());
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

		return player.hasPermission(permission);
	}

    @Override
	public boolean isOnline() {

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
    public RCPlayer getTargetPlayer() {

        return RaidCraft.getPlayer(BukkitUtil.getTarget(player, player.getWorld().getPlayers()));
    }

    @Override
    public BlockWorldVector getTargetBlock() {

        return BukkitUtil.toBlockWorldVector(player.getTargetBlock(null, 100));
    }

    public LivingEntity getTarget() {

        return BukkitUtil.getTargetEntity(player, LivingEntity.class);
    }
}

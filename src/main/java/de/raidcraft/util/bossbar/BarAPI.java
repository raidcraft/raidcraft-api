package de.raidcraft.util.bossbar;

import de.raidcraft.RaidCraftPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * Allows plugins to safely set a health bar message.
 *
 * @author James Mortemore
 */

public class BarAPI implements Listener {

    private static final int ENTITY_ID = 6000;
    private static HashMap<String, FakeDragon> players = new HashMap<>();
    private static HashMap<String, Integer> timers = new HashMap<>();

    private static RaidCraftPlugin plugin;

    public BarAPI(RaidCraftPlugin plugin) {

        BarAPI.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PlayerLoggout(PlayerQuitEvent event) {

        quit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event) {

        quit(event.getPlayer());
    }

    private void quit(Player player) {

        removeBar(player);
    }

    public static void setMessage(Player player, String message) {

        FakeDragon dragon = getDragon(player, message);

        dragon.name = cleanMessage(message);
        dragon.health = FakeDragon.MAX_HEALTH;

        cancelTimer(player);

        sendDragon(dragon, player);

    }

    public static void setMessage(Player player, String message, float percent) {

        FakeDragon dragon = getDragon(player, message);

        dragon.name = cleanMessage(message);
        dragon.health = (percent / 100f) * FakeDragon.MAX_HEALTH;

        cancelTimer(player);

        sendDragon(dragon, player);
    }

    public static void setMessage(final Player player, String message, long ticks) {

        FakeDragon dragon = getDragon(player, message);

        dragon.name = cleanMessage(message);
        dragon.health = FakeDragon.MAX_HEALTH;

        final long dragonHealthMinus = FakeDragon.MAX_HEALTH / ticks;

        cancelTimer(player);

        timers.put(player.getName(), Bukkit.getScheduler().runTaskTimer(plugin, new BukkitRunnable() {

            @Override
            public void run() {

                FakeDragon drag = getDragon(player, "");
                drag.health -= dragonHealthMinus;

                if (drag.health <= 0) {
                    removeBar(player);
                    cancelTimer(player);
                } else {
                    sendDragon(drag, player);
                }
            }

        }, 0L, 1L).getTaskId());

        sendDragon(dragon, player);
    }

    public static boolean hasBar(Player player) {

        return players.get(player.getName()) != null;
    }

    public static void removeBar(Player player) {

        if (!hasBar(player))
            return;

        Object destroyPacket = getDragon(player, "").getDestroyEntityPacket();
        BarAPIUtil.sendPacket(player, destroyPacket);

        players.remove(player.getName());

        cancelTimer(player);
    }

    public static void setHealth(Player player, float percent) {

        if (!hasBar(player))
            return;

        FakeDragon dragon = getDragon(player, "");
        dragon.health = (percent / 100f) * FakeDragon.MAX_HEALTH;

        cancelTimer(player);

        sendDragon(dragon, player);
    }

    private static String cleanMessage(String message) {

        if (message.length() > 64)
            return message.substring(0, 63);

        return message;
    }

    private static void cancelTimer(Player player) {

        Integer timerID = timers.remove(player.getName());

        if (timerID != null) {
            Bukkit.getScheduler().cancelTask(timerID);
        }
    }

    private static void sendDragon(FakeDragon dragon, Player player) {

        Object metaPacket = dragon.getMetadataPacket(dragon.getWatcher());
        Object teleportPacket = dragon.getTeleportPacket(player.getLocation().add(0, -200, 0));

        BarAPIUtil.sendPacket(player, metaPacket);
        BarAPIUtil.sendPacket(player, teleportPacket);
    }

    private static FakeDragon getDragon(Player player, String message) {

        if (hasBar(player)) {
            return players.get(player.getName());
        } else
            return addDragon(player, message);
    }

    private static FakeDragon addDragon(Player player, String message) {

        FakeDragon dragon = new FakeDragon(message, ENTITY_ID, player.getLocation().add(0, -200, 0));

        Object mobPacket = dragon.getMobPacket();
        BarAPIUtil.sendPacket(player, mobPacket);

        players.put(player.getName(), dragon);

        return dragon;
    }
}
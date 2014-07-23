package de.raidcraft.util.bossbar;

import com.comphenix.protocol.ProtocolLibrary;
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

    private static HashMap<String, FakeWither> players = new HashMap<>();
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

        FakeWither bossBar = getBossBar(player, message);

        bossBar.setCustomName(cleanMessage(message));
        bossBar.setHealth(FakeWither.DEFAULT_HEALTH);

        cancelTimer(player);
    }

    public static void setMessage(Player player, String message, float percent) {

        FakeWither bossBar = getBossBar(player, message);

        bossBar.setCustomName(cleanMessage(message));
        bossBar.setHealth((int) ((percent / 100f) * FakeWither.DEFAULT_HEALTH));

        cancelTimer(player);
    }

    public static void setMessage(final Player player, String message, long ticks) {

        setMessage(player, message, ticks, true);
    }

    public static void setMessage(final Player player, String message, long ticks, final boolean countDown) {

        FakeWither bossBar = getBossBar(player, message);

        bossBar.setCustomName(cleanMessage(message));
        bossBar.setHealth(countDown ? FakeWither.DEFAULT_HEALTH : 0);

        final long dragonHealthMinus = FakeWither.DEFAULT_HEALTH / ticks;

        cancelTimer(player);

        timers.put(player.getName(), Bukkit.getScheduler().runTaskTimer(plugin, new BukkitRunnable() {

            @Override
            public void run() {

                FakeWither bossBar = getBossBar(player, "");
                if (countDown) {
                    bossBar.setHealth((int) (bossBar.getHealth() - dragonHealthMinus));
                } else {
                    bossBar.setHealth((int) (bossBar.getHealth() + dragonHealthMinus));
                }

                if (bossBar.getHealth() <= 0) {
                    removeBar(player);
                    cancelTimer(player);
                }
            }

        }, 0L, 1L).getTaskId());
    }

    public static boolean hasBar(Player player) {

        return players.get(player.getName()) != null;
    }

    public static void removeBar(Player player) {

        if (!hasBar(player)) {
            return;
        }

        getBossBar(player, "").destroy();
        players.remove(player.getName());

        cancelTimer(player);
    }

    public static void setHealth(Player player, float percent) {

        if (!hasBar(player)) {
            return;
        }

        FakeWither bossBar = getBossBar(player, "");
        bossBar.setHealth((int) ((percent / 100f) * FakeWither.DEFAULT_HEALTH));

        cancelTimer(player);
    }

    private static String cleanMessage(String message) {

        if (message.length() > 64) {
            return message.substring(0, 63);
        }

        return message;
    }

    private static void cancelTimer(Player player) {

        Integer timerID = timers.remove(player.getName());

        if (timerID != null) {
            Bukkit.getScheduler().cancelTask(timerID);
        }
    }

    private static FakeWither getBossBar(Player player, String message) {

        if (hasBar(player)) {
            return players.get(player.getName());
        } else {
            return addBossBar(player, message);
        }
    }

    private static FakeWither addBossBar(Player player, String message) {

        FakeWither bossBar = new FakeWither(player.getLocation().add(0, -200, 0), ProtocolLibrary.getProtocolManager());

        bossBar.setCustomName(message);
        bossBar.create();

        players.put(player.getName(), bossBar);

        return bossBar;
    }
}
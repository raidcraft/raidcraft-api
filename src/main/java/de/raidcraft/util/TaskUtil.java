package de.raidcraft.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author mdoering
 */
public class TaskUtil {

    public static BukkitTask runTask(Runnable runnable) {

        return Bukkit.getScheduler().runTask(RaidCraft.getComponent(RaidCraftPlugin.class), runnable);
    }

    public static BukkitTask runTaskLater(Runnable runnable, long delay) {

        return Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(RaidCraftPlugin.class), runnable, delay);
    }

    public static BukkitTask runTaskTimer(Runnable runnable, long interval) {

        return runTaskTimerAsynchronously(runnable, interval, interval);
    }

    public static BukkitTask runTaskTimer(Runnable runnable, long interval, long delay) {

        return Bukkit.getScheduler().runTaskTimer(RaidCraft.getComponent(RaidCraftPlugin.class), runnable, interval, delay);
    }

    public static BukkitTask runTaskAsynchronously(Runnable runnable) {

        return Bukkit.getScheduler().runTaskAsynchronously(RaidCraft.getComponent(RaidCraftPlugin.class), runnable);
    }

    public static BukkitTask runTaskLaterAsynchronously(Runnable runnable, long delay) {

        return Bukkit.getScheduler().runTaskLaterAsynchronously(RaidCraft.getComponent(RaidCraftPlugin.class), runnable, delay);
    }

    public static BukkitTask runTaskTimerAsynchronously(Runnable runnable, long interval) {

        return runTaskTimerAsynchronously(runnable, interval, interval);
    }

    public static BukkitTask runTaskTimerAsynchronously(Runnable runnable, long interval, long delay) {

        return Bukkit.getScheduler().runTaskTimerAsynchronously(RaidCraft.getComponent(RaidCraftPlugin.class), runnable, interval, delay);
    }
}

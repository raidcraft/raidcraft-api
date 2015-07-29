package de.raidcraft.api.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.util.TimeUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Timer extends BukkitRunnable {

    private static final Map<UUID, Map<String, Timer>> ACTIVE_TIMERS = new HashMap<>();

    public static Timer startTimer(Player player, ConfigurationSection config) {

        Timer timer;
        if (config.isSet("type")) {
            switch (config.getString("type")) {
                case "interval":
                    timer = new IntervalTimer(player, config);
                    break;
                default:
                    timer = new Timer(player, config);
                    break;
            }
        } else {
            timer = new Timer(player, config);
        }
        timer.start();
        return timer;
    }

    public static boolean resetTimer(Player player, String id) {

        Optional<Timer> activeTimer = getActiveTimer(player, id);
        if (!activeTimer.isPresent()) {
            return false;
        }
        activeTimer.get().reset();
        return true;
    }

    public static Optional<Timer> getActiveTimer(Player player, String id) {

        if (!ACTIVE_TIMERS.containsKey(player.getUniqueId())) {
            return Optional.empty();
        }
        return Optional.ofNullable(ACTIVE_TIMERS.get(player.getUniqueId()).get(id));
    }

    public static boolean hasActiveTimer(Player player) {

        return ACTIVE_TIMERS.containsKey(player.getUniqueId());
    }

    public static boolean isActive(Player player, String id) {

        Optional<Timer> activeTimer = getActiveTimer(player, id);
        return activeTimer.isPresent();
    }

    public static boolean cancelTimer(Player player, String id) {

        Optional<Timer> activeTimer = getActiveTimer(player, id);
        if (!activeTimer.isPresent()) {
            return false;
        }
        activeTimer.get().cancel();
        return true;
    }

    public static boolean endTimer(Player player, String id) {

        Optional<Timer> activeTimer = getActiveTimer(player, id);
        if (!activeTimer.isPresent()) {
            return false;
        }
        // run will end the timer normally
        activeTimer.get().run();
        return true;
    }

    public static Optional<Timer> removeTimer(Timer timer) {

        UUID uniqueId = timer.getPlayer().getUniqueId();
        if (!ACTIVE_TIMERS.containsKey(uniqueId)) {
            return Optional.empty();
        }
        Timer remove = ACTIVE_TIMERS.get(uniqueId).remove(timer.getId());
        if (ACTIVE_TIMERS.get(uniqueId).isEmpty()) {
            ACTIVE_TIMERS.remove(uniqueId);
        }
        return Optional.ofNullable(remove);
    }
    private final String id;
    private final Player player;
    private final Collection<Action<Player>> endActions;
    private final Collection<Action<Player>> cancelActions;
    private long startTime;
    // in ticks
    private long duration;

    private Timer(Player player, ConfigurationSection config) {

        this.id = config.getString("id");
        this.player = player;
        this.duration = TimeUtil.parseTimeAsTicks(config.getString("duration"));
        this.endActions = ActionAPI.createActions(config.getConfigurationSection("end-actions"), Player.class);
        this.cancelActions = ActionAPI.createActions(config.getConfigurationSection("cancel-actions"), Player.class);
    }

    public String getId() {

        if (id == null) {
            return getTaskId() + "";
        }
        return id;
    }

    /**
     * Gets the remaining time in ticks.
     *
     * @return remaining time in ticks
     */
    public long getRemainingTime() {

        long passedTimed = startTime - System.currentTimeMillis();
        return getDuration() - TimeUtil.millisToTicks(passedTimed);
    }

    /**
     * Time to add in ticks.
     * @param time in ticks
     */
    public void addTime(long time) {

        if (startTime > 0) {
            long current = getDuration();
            long passedTimed = startTime - System.currentTimeMillis();
            long newTime = TimeUtil.millisToTicks(TimeUtil.ticksToMillis(getDuration() + time) - passedTimed);
            setDuration(newTime);
            reset();
            setDuration(current + time);
        } else {
            setDuration(getDuration() + time);
            reset();
        }
    }

    public void addTemporaryTime(long time) {

        long current = getDuration();
        addTime(time);
        setDuration(current);
    }

    public void reset() {

        super.cancel();
        startTask();
    }

    public void start() {

        if (getDuration() <= 0) return;
        if (!ACTIVE_TIMERS.containsKey(player.getUniqueId())) {
            ACTIVE_TIMERS.put(player.getUniqueId(), new HashMap<>());
        }
        startTask();
        startTime = System.currentTimeMillis();
        ACTIVE_TIMERS.get(player.getUniqueId()).put(getId(), this);
    }

    protected void startTask() {

        runTaskLater(RaidCraft.getComponent(RaidCraftPlugin.class), getDuration());
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {

        super.cancel();
        removeTimer(this);
        if (!player.isOnline()) return;
        RaidCraft.callEvent(new RCTimerCancelEvent(this));
        cancelActions.forEach(playerAction -> playerAction.accept(player));
    }

    @Override
    public void run() {

        removeTimer(this);
        if (!player.isOnline()) return;
        RaidCraft.callEvent(new RCTimerEndEvent(this));
        endActions.forEach(playerAction -> playerAction.accept(player));
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class IntervalTimer extends Timer {

        private long delay;
        private long interval;
        private BukkitTask cancelTask;

        private IntervalTimer(Player player, ConfigurationSection config) {

            super(player, config);
            this.delay = TimeUtil.parseTimeAsTicks(config.getString("delay", "1"));
            this.interval = TimeUtil.parseTimeAsTicks(config.getString("interval", "1"));
        }

        @Override
        public void run() {

            RaidCraft.callEvent(new RCTimerTickEvent(this));
            super.run();
        }

        @Override
        protected void startTask() {

            RaidCraftPlugin plugin = RaidCraft.getComponent(RaidCraftPlugin.class);
            runTaskTimer(plugin, getDelay(), getInterval());
            cancelTask = Bukkit.getScheduler().runTaskLater(plugin, this::cancel, getDuration());
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {

            super.cancel();
            cancelTask.cancel();
            cancelTask = null;
        }
    }
}

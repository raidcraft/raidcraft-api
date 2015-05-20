package de.raidcraft.api.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.util.TimeUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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

        Timer timer = new Timer(player, config);
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
    private double time;

    private Timer(Player player, ConfigurationSection config) {

        this.id = config.getString("id");
        this.player = player;
        this.time = config.getDouble("time");
        this.endActions = ActionAPI.createActions(config.getConfigurationSection("end-actions"), Player.class);
        this.cancelActions = ActionAPI.createActions(config.getConfigurationSection("cancel-actions"), Player.class);
    }

    public String getId() {

        if (id == null) {
            return getTaskId() + "";
        }
        return id;
    }

    public void addTime(double time) {

        if (startTime > 0) {
            double current = getTime();
            long passedTimed = startTime - System.currentTimeMillis();
            long newTime = TimeUtil.secondsToMillis(getTime() + time) - passedTimed;
            setTime(newTime);
            reset();
            setTime(current + time);
        } else {
            setTime(getTime() + time);
            reset();
        }
    }

    public void addTemporaryTime(double time) {

        double current = getTime();
        addTime(time);
        setTime(current);
    }

    public void reset() {

        cancel();
        start();
    }

    public void start() {

        if (getTime() <= 0) return;
        if (!ACTIVE_TIMERS.containsKey(player.getUniqueId())) {
            ACTIVE_TIMERS.put(player.getUniqueId(), new HashMap<>());
        }
        runTaskLater(RaidCraft.getComponent(RaidCraftPlugin.class), TimeUtil.secondsToTicks(getTime()));
        startTime = System.currentTimeMillis();
        ACTIVE_TIMERS.get(player.getUniqueId()).put(getId(), this);
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {

        super.cancel();
        removeTimer(this);
        if (!player.isOnline()) return;
        cancelActions.forEach(playerAction -> playerAction.accept(player));
    }

    @Override
    public void run() {

        removeTimer(this);
        if (!player.isOnline()) return;
        endActions.forEach(playerAction -> playerAction.accept(player));
    }
}

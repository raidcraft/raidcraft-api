package de.raidcraft.api.action.trigger.global;

import de.raidcraft.api.action.RCTimerCancelEvent;
import de.raidcraft.api.action.RCTimerEndEvent;
import de.raidcraft.api.action.RCTimerTickEvent;
import de.raidcraft.api.action.trigger.Trigger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author mdoering
 */
public class TimerTrigger extends Trigger implements Listener {

    public TimerTrigger() {

        super("timer", "tick", "end", "cancel");
    }

    @Information(
            value = "timer.tick",
            desc = "Called when a timer ticks.",
            conf = {
                    "id: unique id of the timer"
            }
    )
    @EventHandler(ignoreCancelled = true)
    public void onTimerTick(RCTimerTickEvent event) {

        informListeners("tick", event.getTimer().getPlayer(), config -> event.getTimer().getId().equalsIgnoreCase(config.getString("id")));
    }

    @Information(
            value = "timer.end",
            desc = "Called when a timer ends.",
            conf = {
                    "id: unique id of the timer"
            }
    )
    @EventHandler(ignoreCancelled = true)
    public void onTimerEnd(RCTimerEndEvent event) {

        informListeners("end", event.getTimer().getPlayer(), config -> event.getTimer().getId().equalsIgnoreCase(config.getString("id")));
    }

    @Information(
            value = "timer.cancel",
            desc = "Called when a timer is cancelled.",
            conf = {
                    "id: unique id of the timer"
            }
    )
    @EventHandler(ignoreCancelled = true)
    public void onTimerCancel(RCTimerCancelEvent event) {

        informListeners("cancel", event.getTimer().getPlayer(), config -> event.getTimer().getId().equalsIgnoreCase(config.getString("id")));
    }
}

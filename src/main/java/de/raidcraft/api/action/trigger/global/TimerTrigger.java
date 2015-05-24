package de.raidcraft.api.action.trigger.global;

import de.raidcraft.api.action.RCTimerTickEvent;
import de.raidcraft.api.action.trigger.Trigger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author mdoering
 */
public class TimerTrigger extends Trigger implements Listener {

    public TimerTrigger() {

        super("timer", "tick");
    }

    @EventHandler(ignoreCancelled = true)
    public void onTimerTick(RCTimerTickEvent event) {

        informListeners("tick", event.getTimer().getPlayer(), config -> event.getTimer().getId().equalsIgnoreCase(config.getString("id")));
    }
}

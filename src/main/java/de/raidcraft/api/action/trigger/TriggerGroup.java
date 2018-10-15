package de.raidcraft.api.action.trigger;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.TriggerFactory;
import de.raidcraft.api.action.requirement.Requirement;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Data
public abstract class TriggerGroup extends Trigger implements TriggerListener<Player> {

    private String description = "";
    private boolean enabled = true;
    private boolean ordered = false;
    private int required = 0;

    private final List<TriggerFactory> trigger;
    // this set is used to track the requirement count of executed triggers
    // we cannot simply track the trigger identifier because
    // there can be multiple of the same trigger with different configs
    private final Set<TriggerListenerConfigWrapper> executedTrigger = new HashSet<>();
    // tracks the index of the current active trigger
    // is only used for ordered triggers
    private int currentTriggerIndex = -1;
    private TriggerListenerConfigWrapper currentTriggerWrapper = null;
    // task that resets the ordered triggers to the last position after the configured delay
    // is cancelled if a new trigger is executed in time
    private BukkitTask resetTask = null;

    protected TriggerGroup(String identifier) {
        super(identifier);
        this.trigger = loadTrigger();
    }

    protected abstract List<TriggerFactory> loadTrigger();

    @Override
    public Class<Player> getTriggerEntityType() {
        return Player.class;
    }

    public Optional<TriggerListenerConfigWrapper> getCurrentTriggerWrapper() {
        return Optional.ofNullable(this.currentTriggerWrapper);
    }

    public Optional<BukkitTask> getResetTask() {
        return Optional.ofNullable(resetTask);
    }

    public void registerListeners() {
        if (!isOrdered()) {
            getTrigger().forEach(triggerFactory -> triggerFactory.registerListener(this));
        } else {
            updateListeners();
        }
    }

    public void unregisterListeners() {
        getTrigger().forEach(triggerFactory -> triggerFactory.unregisterListener(this));
    }

    private void unregisterListener(int triggerIndex) {

        if (triggerIndex < 0 || triggerIndex >= getTrigger().size()) return;
        currentTriggerWrapper = null;
        getTrigger().get(triggerIndex).unregisterListener(this);
    }

    private void registerListener(int triggerIndex) {

        if (triggerIndex < 0 || triggerIndex >= getTrigger().size()) return;
        getTrigger().get(triggerIndex).registerListener(this)
                .ifPresent(this::setCurrentTriggerWrapper);
    }

    @SuppressWarnings("unchecked")
    private void resetListenerTo(int triggerIndex, Player player) {

        setResetTask(null);
        if (currentTriggerIndex < 0 || currentTriggerIndex >= getTrigger().size()) return;
        if (triggerIndex < 0 || triggerIndex >= getTrigger().size()) return;

        unregisterListener(getCurrentTriggerIndex());
        for (int i = getCurrentTriggerIndex(); i >= triggerIndex; i--) {
            if (getTrigger().size() <= i) continue;
            getTrigger().get(i).getRequirements().stream()
                    .filter(requirement -> ActionAPI.matchesType(requirement, Player.class))
                    .map(requirement -> (Requirement<Player>) requirement)
                    .forEach(requirement -> requirement.delete(player));
        }
        setCurrentTriggerIndex(triggerIndex);
        registerListener(triggerIndex);
    }

    private void updateListeners() {
        if (getTrigger().size() < 1) return;
        if (currentTriggerIndex < 0 || currentTriggerIndex >= getTrigger().size()) return;

        unregisterListener(getCurrentTriggerIndex());

        this.currentTriggerIndex++;
        if (getCurrentTriggerIndex() >= getTrigger().size()) {
            // this will reset our trigger to the starting position
            this.currentTriggerIndex = -1;
            updateListeners();
        } else {
            registerListener(getCurrentTriggerIndex());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean processTrigger(Player player, TriggerListenerConfigWrapper trigger) {

        if (isEnabled()) {
            unregisterListeners();
            return false;
        }

        // cancel the current running reset task because we reached the new trigger in time
        getResetTask().ifPresent(task -> {
            task.cancel();
            setResetTask(null);
        });

        // check if we reached the end of our ordered trigger list
        if (getCurrentTriggerIndex() == getTrigger().size() - 1) {
            // calling this at the end of our trigger list
            // will reset the trigger to the start
            // but first delete all requirements of all triggers
            getTrigger().stream()
                    .flatMap(triggerFactory -> triggerFactory.getRequirements().stream())
                    .filter(requirement -> ActionAPI.matchesType(requirement, Player.class))
                    .map(requirement -> (Requirement<Player>) requirement)
                    .forEach(requirement -> requirement.delete(player));
            updateListeners();
        } else if (getCurrentTriggerIndex() > -1) {
            if (trigger.getValid() > 0) {
                final int currentIndex = getCurrentTriggerIndex();
                setResetTask(Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(RaidCraftPlugin.class),
                        () -> resetListenerTo(currentIndex, player),
                        trigger.getValid()));
            }
            // we did not reach the end, so update the list of triggers
            updateListeners();
            return false;
        }

        if (getRequired() > 0) {
            executedTrigger.add(trigger);
            return getRequired() <= executedTrigger.size();
        }

        return true;
    }
}

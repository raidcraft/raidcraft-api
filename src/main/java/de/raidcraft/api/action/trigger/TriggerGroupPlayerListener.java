package de.raidcraft.api.action.trigger;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.requirement.Requirement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"triggerGroup", "player"})
public class TriggerGroupPlayerListener implements TriggerListener<Player> {

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

    private final TriggerGroup triggerGroup;
    private final Player player;

    public TriggerGroupPlayerListener(TriggerGroup triggerGroup, Player player) {
        this.triggerGroup = triggerGroup;
        this.player = player;
    }

    @Override
    public Class<Player> getTriggerEntityType() {
        return Player.class;
    }

    @Override
    public Optional<Player> getEntity() {
        return Optional.ofNullable(getPlayer());
    }

    public Optional<TriggerListenerConfigWrapper> getCurrentTriggerWrapper() {
        return Optional.ofNullable(this.currentTriggerWrapper);
    }

    public Optional<BukkitTask> getResetTask() {
        return Optional.ofNullable(resetTask);
    }

    public void registerListeners() {
        if (!getTriggerGroup().isOrdered()) {
            getTriggerGroup().getTrigger().forEach(triggerFactory -> triggerFactory.registerListener(this));
        } else {
            updateListeners();
        }
    }

    public void unregisterListeners() {
        getTriggerGroup().getTrigger().forEach(triggerFactory -> triggerFactory.unregisterListener(this));
    }

    private void unregisterListener(int triggerIndex) {

        if (triggerIndex < 0 || triggerIndex >= getTriggerGroup().getTrigger().size()) return;
        currentTriggerWrapper = null;
        getTriggerGroup().getTrigger().get(triggerIndex).unregisterListener(this);
    }

    private void registerListener(int triggerIndex) {

        if (triggerIndex < 0 || triggerIndex >= getTriggerGroup().getTrigger().size()) return;
        getTriggerGroup().getTrigger().get(triggerIndex).registerListener(this)
                .ifPresent(this::setCurrentTriggerWrapper);
    }

    @SuppressWarnings("unchecked")
    private void resetListenerTo(int triggerIndex, Player player) {

        setResetTask(null);
        if (currentTriggerIndex < 0 || currentTriggerIndex >= getTriggerGroup().getTrigger().size()) return;
        if (triggerIndex < 0 || triggerIndex >= getTriggerGroup().getTrigger().size()) return;

        unregisterListener(getCurrentTriggerIndex());
        for (int i = getCurrentTriggerIndex(); i >= triggerIndex; i--) {
            if (getTriggerGroup().getTrigger().size() <= i) continue;
            getTriggerGroup().getTrigger().get(i).getRequirements().stream()
                    .filter(requirement -> ActionAPI.matchesType(requirement, Player.class))
                    .map(requirement -> (Requirement<Player>) requirement)
                    .forEach(requirement -> requirement.delete(player));
        }
        setCurrentTriggerIndex(triggerIndex);
        registerListener(triggerIndex);
    }

    private void updateListeners() {
        if (getTriggerGroup().getTrigger().size() < 1) return;

        unregisterListener(getCurrentTriggerIndex());

        this.currentTriggerIndex++;
        if (getCurrentTriggerIndex() >= getTriggerGroup().getTrigger().size()) {
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

        if (!getTriggerGroup().isEnabled()) {
            unregisterListeners();
            return false;
        }

        if (getTriggerGroup().isOrdered() && !getCurrentTriggerWrapper().map(wrapper -> wrapper.equals(trigger)).orElse(false)) {
            return false;
        }

        // cancel the current running reset task because we reached the new trigger in time
        getResetTask().ifPresent(task -> {
            task.cancel();
            setResetTask(null);
        });

        // check if we reached the end of our ordered trigger list
        if (getCurrentTriggerIndex() == getTriggerGroup().getTrigger().size() - 1) {
            // calling this at the end of our trigger list
            // will reset the trigger to the start
            // but first delete all requirements of all triggers
            getTriggerGroup().getTrigger().stream()
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

        if (getTriggerGroup().getRequired() > 0) {
            executedTrigger.add(trigger);
            return getTriggerGroup().getRequired() <= executedTrigger.size();
        }

        getTriggerGroup().informListeners(player);
        return true;
    }
}

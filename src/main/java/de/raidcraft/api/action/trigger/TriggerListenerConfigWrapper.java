package de.raidcraft.api.action.trigger;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.RequirementConfigWrapper;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.util.TimeUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Silthus
 */
@ToString(of = {"triggerListener", "config"})
@EqualsAndHashCode(of = {"triggerListener", "config"})
@Data
class TriggerListenerConfigWrapper<T> {

    private final TriggerListener<T> triggerListener;
    private final ConfigurationSection config;
    private final boolean executeOnce;
    private final long cooldown;
    private final long triggerDelay;
    private final long actionDelay;
    private final List<String> worlds = new ArrayList<>();
    private Collection<Action<T>> actions = new ArrayList<>();
    private List<Requirement<T>> requirements = new ArrayList<>();

    protected TriggerListenerConfigWrapper(TriggerListener<T> triggerListener, ConfigurationSection config) {

        this.triggerListener = triggerListener;
        this.config = config;
        this.executeOnce = config.getBoolean("execute-once", false);
        this.cooldown = TimeUtil.parseTimeAsTicks(config.getString("cooldown"));
        this.triggerDelay = TimeUtil.parseTimeAsTicks(config.getString("delay"));
        this.actionDelay = TimeUtil.parseTimeAsTicks(config.getString("action-delay"));
        this.worlds.addAll(config.getStringList("worlds"));
        this.actions = ActionAPI.createActions(config.getConfigurationSection("actions"), triggerListener.getTriggerEntityType());
        this.requirements = ActionAPI.createRequirements(triggerListener.getListenerId(), config.getConfigurationSection("requirements"), triggerListener.getTriggerEntityType());
        if (isExecuteOnce()) {
            // lets add our execute once requirement last
            // this requirement will return false after is has been checked once
            Optional<Requirement<T>> optional = ActionAPI.Helper.createExecuteOnceRequirement(
                    getTriggerListener().getListenerId(),
                    getTriggerListener().getTriggerEntityType());
            if (optional.isPresent()) this.requirements.add(optional.get());
        } else if (cooldown > 0) {
            Optional<Requirement<T>> cooldownRequirement = ActionAPI.Helper.createCooldownRequirement(
                    getTriggerListener().getListenerId(),
                    cooldown,
                    getTriggerListener().getTriggerEntityType());
            if (cooldownRequirement.isPresent()) this.requirements.add(cooldownRequirement.get());
        }
    }

    protected boolean test(T triggeringEntity, Predicate<ConfigurationSection> predicate) {

        if (!worlds.isEmpty()) {
            if (triggeringEntity instanceof Player) {
                String worldName = ((Player) triggeringEntity).getWorld().getName();
                if (!worlds.contains(worldName)) {
                    return false;
                }
            } else {
                if (Bukkit.getWorlds().stream().filter(world -> worlds.contains(world.getName())).count() < 1) return false;
            }
        }
        ConfigurationSection args = config.getConfigurationSection("args");
        if (args == null) args = config.createSection("args");
        return triggerListener.getTriggerEntityType().isAssignableFrom(triggeringEntity.getClass())
                && predicate.test(args)
                && requirements.stream().allMatch(requirement -> requirement.test(triggeringEntity));
    }


    protected void executeActions(T triggeringEntity) {

        Runnable runnable = () -> {

            actions.forEach(action -> action.accept(triggeringEntity));
            if (isExecuteOnce()) {
                // lets get the last requirement which will be the executed once requirement
                // and set the checked key to false
                Requirement<T> requirement = requirements.get(requirements.size() - 1);
                if (requirement instanceof RequirementConfigWrapper) {
                    ((RequirementConfigWrapper<T>)requirement).setChecked(triggeringEntity, false);
                    requirement.save();
                }
            }
        };
        if (getActionDelay() > 0) {
            Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(RaidCraftPlugin.class), runnable, getActionDelay());
        } else {
            runnable.run();
        }
    }
}

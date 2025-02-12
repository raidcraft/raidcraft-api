package de.raidcraft.api.action.trigger;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.RequirementConfigWrapper;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.util.LocationUtil;
import de.raidcraft.util.TimeUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Silthus
 */
@ToString(of = {"identifier", "triggerListener", "config"})
@EqualsAndHashCode(of = {"identifier", "triggerListener", "config", "actions", "requirements", "worlds"})
@Data
public class TriggerListenerConfigWrapper<T> {

    private final String identifier;
    private final TriggerListener<T> triggerListener;
    private final ConfigurationSection config;
    private final boolean executeOnce;
    private final boolean cancel;
    private final int priority;
    private final long cooldown;
    private final long triggerDelay;
    private final long actionDelay;
    // the time this trigger is valid and other triggers after him can be executed
    // only relevant for ordered triggers.
    private final long valid;
    private final int count;
    private final String countText;
    private final List<String> worlds = new ArrayList<>();
    private final List<String> regions = new ArrayList<>();
    private final List<Action<T>> actions = new ArrayList<>();
    private final List<Requirement<T>> requirements = new ArrayList<>();

    protected TriggerListenerConfigWrapper(String identifier, TriggerListener<T> triggerListener, ConfigurationSection config) {

        this.identifier = identifier;
        this.triggerListener = triggerListener;
        this.config = config;
        this.executeOnce = config.getBoolean("execute-once", false);
        this.cancel = config.getBoolean("cancel", false);
        this.priority = config.getInt("priority", 100);
        this.cooldown = TimeUtil.parseTimeAsTicks(config.getString("cooldown"));
        this.triggerDelay = TimeUtil.parseTimeAsTicks(config.getString("delay"));
        this.actionDelay = TimeUtil.parseTimeAsTicks(config.getString("action-delay"));
        this.valid = TimeUtil.parseTimeAsTicks(config.getString("valid"));
        this.count = config.getInt("count");
        this.countText = config.getString("count-text");
        this.worlds.addAll(config.getStringList("worlds"));
        this.regions.addAll(config.getStringList("regions"));
        this.setActions(ActionAPI.createActions(config.getConfigurationSection("actions"), triggerListener.getTriggerEntityType()));
        this.setRequirements(ActionAPI.createRequirements(triggerListener.getListenerId(), config.getConfigurationSection("requirements"), triggerListener.getTriggerEntityType()));
    }

    public ConfigurationSection getArgs() {
        ConfigurationSection args = getConfig().getConfigurationSection("args");
        return args == null ? getConfig().createSection("args") : args;
    }

    private void setExtraRequirements() {
        if (getCount() > 0) {
            getCountRequirement().ifPresent(requirements::add);
        }
        if (isExecuteOnce()) {
            // lets add our execute once requirement last
            // this requirement will return false after is has been checked once
            getExecuteOnceRequirement().ifPresent(requirements::add);
        } else if (cooldown > 0) {
            getCooldownRequirement().ifPresent(requirements::add);
        }
    }

    private Optional<Requirement<T>> getExecuteOnceRequirement() {
        return ActionAPI.Helper.createExecuteOnceRequirement(
                getTriggerListener().getListenerId(),
                getTriggerListener().getTriggerEntityType());
    }

    private Optional<Requirement<T>> getCooldownRequirement() {
        return ActionAPI.Helper.createCooldownRequirement(
                getTriggerListener().getListenerId(),
                cooldown,
                getTriggerListener().getTriggerEntityType());
    }

    private Optional<Requirement<T>> getCountRequirement() {
        return ActionAPI.Helper.createCountRequirement(
                getTriggerListener().getListenerId(),
                getCount(),
                getCountText(),
                getTriggerListener().getTriggerEntityType()
        );
    }

    public void setActions(List<Action<T>> actions) {
        this.actions.clear();
        this.actions.addAll(actions);
    }

    public void setRequirements(List<Requirement<T>> requirements) {
        this.requirements.clear();
        this.requirements.addAll(requirements);
        this.setExtraRequirements();
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
        if (!regions.isEmpty() && triggeringEntity instanceof Player && RaidCraft.getWorldGuard() != null) {
            Optional<ApplicableRegionSet> applicableRegions = LocationUtil.getWorldGuardRegions(((Player) triggeringEntity).getLocation());
            if (applicableRegions.isPresent() && applicableRegions.get().getRegions().stream().map(ProtectedRegion::getId).noneMatch(regions::contains)) {
                return false;
            }
        }
        return triggerListener.getTriggerEntityType().isAssignableFrom(triggeringEntity.getClass())
                && predicate.test(getArgs())
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

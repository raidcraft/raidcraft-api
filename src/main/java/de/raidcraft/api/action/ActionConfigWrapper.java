package de.raidcraft.api.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.RevertableAction;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.util.TimeUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Silthus
 */
@EqualsAndHashCode(of = {"action", "config"})
@Data
class ActionConfigWrapper<T> implements RevertableAction<T> {

    private final Class<T> type;
    private final Action<T> action;
    private final ConfigurationSection config;
    private final long delay;
    private final long cooldown;
    private final boolean executeOnce;
    private List<Requirement<T>> requirements = new ArrayList<>();

    protected ActionConfigWrapper(Action<T> action, ConfigurationSection config, Class<T> type) {

        this.type = type;
        this.action = action;
        this.config = config;
        this.delay = TimeUtil.parseTimeAsTicks(config.getString("delay"));
        this.cooldown = TimeUtil.parseTimeAsTicks(config.getString("cooldown"));
        this.executeOnce = config.getBoolean("execute-once", false);
        this.requirements = ActionAPI.createRequirements(getIdentifier(), config.getConfigurationSection("requirements"), type);
        if (isExecuteOnce()) {
            // lets add our execute once requirement last
            // this requirement will return false after is has been checked once
            Optional<Requirement<T>> optional = ActionAPI.Helper.createExecuteOnceRequirement(
                    getIdentifier(),
                    getType());
            if (optional.isPresent()) this.requirements.add(optional.get());
        } else if (cooldown > 0) {
            Optional<Requirement<T>> cooldownRequirement = ActionAPI.Helper.createCooldownRequirement(
                    getIdentifier(),
                    cooldown,
                    getType());
            if (cooldownRequirement.isPresent()) this.requirements.add(cooldownRequirement.get());
        }
    }

    public ConfigurationSection getConfig() {

        ConfigurationSection args = this.config.getConfigurationSection("args");
        if (args == null) args = this.config.createSection("args");
        return args;
    }

    public Action<T> with(String key, Object value) {

        config.set(key, value);
        return this;
    }

    public Action<T> withArgs(String key, Object value) {

        getConfig().set(key, value);
        return this;
    }

    @Override
    public void addRequirement(Requirement<T> requirement) {

        requirements.add(requirement);
    }

    public void accept(T type) {

        accept(type, getConfig());
    }

    public void accept(T type, ConfigurationSection config) {

        Runnable runnable = () -> {
            if (!requirements.isEmpty()) {
                boolean allMatch = requirements.stream()
                        .allMatch(requirement -> requirement.test(type));
                if (!allMatch) return;
            }
            action.accept(type, config);
            if (isExecuteOnce()) {
                Requirement<T> executeOnce = requirements.get(requirements.size() - 1);
                if (executeOnce instanceof RequirementConfigWrapper) {
                    ((RequirementConfigWrapper<T>)executeOnce).setChecked(type, false);
                    executeOnce.save();
                }
            }
        };
        if (delay > 0) {
            Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(RaidCraftPlugin.class), runnable, delay);
        } else {
            runnable.run();
        }
    }

    @Override
    public void revert(T type) {

        revert(type, getConfig());
        if (isExecuteOnce()) {
            Requirement<T> executeOnce = requirements.get(requirements.size() - 1);
            if (executeOnce instanceof RequirementConfigWrapper) {
                ((RequirementConfigWrapper<T>)executeOnce).setChecked(type, true);
                executeOnce.save();
            }
        }
    }

    @Override
    public void revert(T type, ConfigurationSection config) {

        if (action instanceof RevertableAction) {
            ((RevertableAction<T>) action).revert(type, config);
        }
    }
}

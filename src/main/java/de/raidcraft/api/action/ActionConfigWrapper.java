package de.raidcraft.api.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ContextualAction;
import de.raidcraft.api.action.action.RevertableAction;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.util.TimeUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Silthus
 */
@EqualsAndHashCode(of = { "action", "config" })
@Data
public class ActionConfigWrapper<T> implements RevertableAction<T> {

    public static <T> ActionConfigWrapper<T> of(Action<T> action, Class<T> actionClass) {

        return new ActionConfigWrapper<>(action, new MemoryConfiguration(), actionClass);
    }

    private final Class<T> type;
    private final Action<T> action;
    private final ConfigurationSection config;
    private final long delay;
    private final long cooldown;
    private final boolean executeOnce;
    private boolean executeChildActions;
    private Player player;
    private List<Action<?>> actions = new ArrayList<>();
    private List<Requirement<?>> requirements = new ArrayList<>();

    protected ActionConfigWrapper(Action<T> action, ConfigurationSection config, Class<T> type) {

        this.type = type;
        this.action = action;
        this.config = config;
        this.delay = TimeUtil.parseTimeAsTicks(config.getString("delay"));
        this.cooldown = TimeUtil.parseTimeAsTicks(config.getString("cooldown"));
        this.executeOnce = config.getBoolean("execute-once", false);
        this.executeChildActions = config.getBoolean("execute-child-actions", true);
        this.actions = ActionAPI.createActions(config.getConfigurationSection("actions"));
        this.requirements = ActionAPI.createRequirements(getIdentifier(),
                config.getConfigurationSection("requirements"));
        if (isExecuteOnce()) {
            // lets add our execute once withRequirement last
            // this requirement will return false after is has been checked once
            Optional<Requirement<T>> optional = ActionAPI.Helper.createExecuteOnceRequirement(getIdentifier(),
                    getType());
            if (optional.isPresent())
                this.requirements.add(optional.get());
        } else if (cooldown > 0) {
            Optional<Requirement<T>> cooldownRequirement = ActionAPI.Helper.createCooldownRequirement(getIdentifier(),
                    cooldown, getType());
            if (cooldownRequirement.isPresent())
                this.requirements.add(cooldownRequirement.get());
        }
    }

    public ConfigurationSection getConfig() {

        ConfigurationSection args = this.config.getConfigurationSection("args");
        if (args == null)
            args = this.config.createSection("args");
        return args;
    }

    @Override
    public Action<T> with(String key, Object value) {

        getConfig().set(key, value);
        return this;
    }

    @Override
    public Action<T> withArgs(String key, Object value) {

        getConfig().set(key, value);
        return this;
    }

    @Override
    public Action<T> withPlayer(Player player) {

        this.player = player;
        return this;
    }

    protected Optional<Player> getPlayer() {
        return Optional.ofNullable(this.player);
    }

    @Override
    public void addRequirement(Requirement<?> requirement) {

        requirements.add(requirement);
    }

    public void accept(T type) {

        accept(type, getConfig());
    }

    @SuppressWarnings("unchecked")
    public void accept(T type, ConfigurationSection config) {

        RaidCraftPlugin plugin = RaidCraft.getComponent(RaidCraftPlugin.class);
        Runnable runnable = () -> {
            if (plugin.getConfig().debugActions) {
                plugin.getLogger().info("PRE ACTION CHECK: " + ActionAPI.getIdentifier(getAction()));
            }
            if (!requirements.isEmpty()) {
                boolean allMatch = true;
                for (Requirement<?> requirement : requirements) {
                    if (!allMatch)
                        break;
                    if (getPlayer().isPresent() && ActionAPI.matchesType(requirement, Player.class)) {
                        allMatch = ((Requirement<Player>) requirement).test(getPlayer().get());
                    } else if (ActionAPI.matchesType(requirement, getType())) {
                        allMatch = ((Requirement<T>) requirement).test(type);
                    } else if (type instanceof Player && ActionAPI.matchesType(requirement, Player.class)) {
                        allMatch = ((Requirement<Player>) requirement).test((Player) type);
                    }
                }
                if (plugin.getConfig().debugActions && !allMatch) {
                    plugin.getLogger().info("PRE ACTION CHECK FAILED: " + ActionAPI.getIdentifier(getAction()));
                }
                if (!allMatch)
                    return;
            }

            if (action instanceof ContextualAction) {
                ((ContextualAction<T>) action).accept(type, this, config);
            } else {
                action.accept(type, config);
            }
            if(isExecuteChildActions()) executeChildActions(type);

            if (plugin.getConfig().debugActions) {
                plugin.getLogger().info("ACTION EXECUTED: " + ActionAPI.getIdentifier(getAction()));
            }
            if (isExecuteOnce()) {
                Requirement<?> executeOnce = requirements.get(requirements.size() - 1);
                if (executeOnce instanceof RequirementConfigWrapper) {
                    ((RequirementConfigWrapper<T>) executeOnce).setChecked(type, false);
                    executeOnce.save();
                }
            }
            player = null;
        };
        if (delay > 0) {
            Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        } else {
            runnable.run();
        }
    }

    @SuppressWarnings("unchecked")
    public void executeChildActions(T type) {
        for (Action<?> action : actions) {
            if (ActionAPI.matchesType(action, Player.class)) {
                Player player = getPlayer().orElse((Player) type);
                ((Action<Player>) action).accept(player);
            } else if (ActionAPI.matchesType(action, Conversation.class)) {
                Player player = getPlayer().orElse((Player) type);
                Conversations.getActiveConversation(player)
                        .ifPresent(((Action<Conversation>) action)::accept);
            } else if (ActionAPI.matchesType(action, getType())) {
                ((Action<T>) action).accept(type);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void revert(T type) {

        revert(type, getConfig());
        if (isExecuteOnce()) {
            Requirement<?> executeOnce = requirements.get(requirements.size() - 1);
            if (executeOnce instanceof RequirementConfigWrapper) {
                ((RequirementConfigWrapper<T>) executeOnce).setChecked(type, true);
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

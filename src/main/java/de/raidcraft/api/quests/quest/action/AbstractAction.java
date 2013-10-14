package de.raidcraft.api.quests.quest.action;

import de.raidcraft.util.TimeUtil;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public abstract class AbstractAction<T> implements Action<T> {

    private final int id;
    private final String name;
    private final T provider;
    private final long delay;
    private final long cooldown;
    private final boolean executeOnce;

    public AbstractAction(int id, T provider, ConfigurationSection data) {

        this.id = id;
        this.name = data.getString("type");
        this.provider = provider;
        this.delay = TimeUtil.secondsToTicks(data.getDouble("delay"));
        this.cooldown = TimeUtil.secondsToTicks(data.getDouble("cooldown"));
        this.executeOnce = data.getBoolean("execute-once", false);
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public boolean isExecuteOnce() {

        return executeOnce;
    }

    @Override
    public T getProvider() {

        return provider;
    }

    @Override
    public long getDelay() {

        return delay;
    }

    @Override
    public long getCooldown() {

        return cooldown;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractAction)) return false;

        AbstractAction that = (AbstractAction) o;

        return id == that.id && name.equals(that.name);
    }

    @Override
    public int hashCode() {

        int result = id;
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {

        return name;
    }
}

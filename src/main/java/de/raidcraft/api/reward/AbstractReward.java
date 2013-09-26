package de.raidcraft.api.reward;

import de.raidcraft.util.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip Urban
 */
public abstract class AbstractReward<T> implements Reward<T> {

    private final String name;

    protected AbstractReward(ConfigurationSection config) {

        this.name = StringUtils.formatName(config.getString("name"));
    }

    public abstract void load(ConfigurationSection config);
}

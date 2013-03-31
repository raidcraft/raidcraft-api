package de.raidcraft.api.requirement;

import de.raidcraft.util.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public abstract class AbstractRequirement<T extends RequirementResolver> implements Requirement<T> {

    private final T resolver;
    private final String name;

    public AbstractRequirement(T resolver, ConfigurationSection config) {

        this.resolver = resolver;
        this.name = StringUtils.formatName(config.getName());
    }

    @Override
    public T getResolver() {

        return resolver;
    }

    @Override
    public String getName() {

        return name;
    }

    protected abstract void load(ConfigurationSection data);
}

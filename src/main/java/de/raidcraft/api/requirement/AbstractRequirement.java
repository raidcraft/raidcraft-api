package de.raidcraft.api.requirement;

import de.raidcraft.util.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public abstract class AbstractRequirement<T> implements Requirement<T> {

    private final RequirementResolver<T> resolver;
    private final String name;

    public AbstractRequirement(RequirementResolver<T> resolver, ConfigurationSection config) {

        this.resolver = resolver;
        this.name = StringUtils.formatName(config.getName());
    }

    @Override
    public RequirementResolver<T> getResolver() {

        return resolver;
    }

    @Override
    public String getName() {

        return name;
    }

    protected abstract void load(ConfigurationSection data);
}

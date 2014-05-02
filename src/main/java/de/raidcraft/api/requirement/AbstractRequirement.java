package de.raidcraft.api.requirement;

import de.raidcraft.util.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @deprecated see {@link de.raidcraft.api.action.requirement.Requirement} API
 */
@Deprecated
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

    @Override
    public String getDescription() {

        return "";
    }

    protected abstract void load(ConfigurationSection data);
}

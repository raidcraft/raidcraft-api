package de.raidcraft.api.requirement;

import de.raidcraft.RaidCraft;
import de.raidcraft.util.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public final class RequirementManager {

    private static final Map<String, Class<? extends Requirement<?>>> requirementClasses = new HashMap<>();
    private static final Map<Class<? extends Requirement<?>>, Constructor<? extends Requirement<?>>> constructors = new HashMap<>();

    private RequirementManager() {}

    @SuppressWarnings("unchecked")
    public static <T> List<Requirement<T>> createRequirements(T resolver, ConfigurationSection config) {

        List<Requirement<T>> requirements = new ArrayList<>();
        if (config.getKeys(false) == null) return requirements;
        for (String key : config.getKeys(false)) {
            key = StringUtils.formatName(key);
            if (requirementClasses.containsKey(key)) {
                if (config.getConfigurationSection(key).getKeys(false) == null) continue;

                Class<? extends Requirement<?>> rClass = requirementClasses.get(key);
                for (String reqName : config.getConfigurationSection(key).getKeys(false)) {
                    try {
                        Requirement<T> requirement = (Requirement<T>) constructors.get(rClass).newInstance(
                                resolver,
                                config.getConfigurationSection(key + "." + reqName));
                        if (requirement instanceof AbstractRequirement) {
                            ((AbstractRequirement) requirement).load(config.getConfigurationSection(key + "." + reqName));
                        }
                        requirements.add(requirement);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        RaidCraft.LOGGER.warning(e.getMessage());
                        e.printStackTrace();
                    }
                }
            } else {
                RaidCraft.LOGGER.warning("There are no requirement types defined for " + key + " in " + config);
            }
        }
        return requirements;
    }

    @SuppressWarnings("unchecked")
    public static void registerRequirementType(Class<? extends Requirement<?>> rClass) {

        if (!rClass.isAnnotationPresent(RequirementInformation.class)) {
            RaidCraft.LOGGER.warning("Cannot register " + rClass.getCanonicalName() + " as Requirement because it has no Information tag!");
            return;
        }
        for (Constructor<?> constructor : rClass.getConstructors()) {
            if (constructor.getParameterTypes()[1].isAssignableFrom(ConfigurationSection.class)) {
                constructor.setAccessible(true);
                constructors.put(rClass, (Constructor<? extends Requirement<?>>) constructor);
                // get the name for aliasing
                String name = StringUtils.formatName(rClass.getAnnotation(RequirementInformation.class).value());
                requirementClasses.put(name, rClass);
                break;
            }
        }
    }
}

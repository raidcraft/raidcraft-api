package de.raidcraft.api.requirement;

import de.raidcraft.RaidCraft;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @deprecated see {@link de.raidcraft.api.action.requirement.RequirementFactory}
 */
@Deprecated
public final class RequirementManager {

    private static final Map<String, Class<? extends Requirement<?>>> requirementClasses = new CaseInsensitiveMap<>();
    private static final Map<Class<? extends Requirement<?>>, Constructor<? extends Requirement<?>>> constructors = new HashMap<>();

    private RequirementManager() {}

    @SuppressWarnings("unchecked")
    public static <O> List<Requirement<O>> createRequirements(RequirementResolver<O> resolver, ConfigurationSection config) {

        List<Requirement<O>> requirements = new ArrayList<>();
        if (config == null || config.getKeys(false) == null) {
            return requirements;
        }

        if (config.getKeys(false) == null) return requirements;
        for (String key : config.getKeys(false)) {
//            key = StringUtils.formatName(key);
            if (requirementClasses.containsKey(key)) {
                if (config.getConfigurationSection(key).getKeys(false) == null) continue;

                Class<? extends Requirement<?>> rClass = requirementClasses.get(key);
                for (String reqName : config.getConfigurationSection(key).getKeys(false)) {
                    try {
                        final ConfigurationSection section = config.getConfigurationSection(key + "." + reqName);
                        if (section == null) {
                            RaidCraft.LOGGER.warning("Wrong requirement section " + key + "." + reqName + " defined for " + resolver);
                            continue;
                        }
                        final Requirement<O> requirement = (Requirement<O>) constructors.get(rClass).newInstance(
                                resolver,
                                section);
                        if (requirement instanceof AbstractRequirement) {
                            ((AbstractRequirement) requirement).load(section);
                        }
                        requirements.add(requirement);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        RaidCraft.LOGGER.warning(e.getMessage());
                        e.printStackTrace();
                    }
                }
            } else {
                RaidCraft.LOGGER.warning("There are no requirement types defined for the type " + key);
                RaidCraft.LOGGER.warning("Available Requirement Types are: " + Strings.join(new ArrayList<>(requirementClasses.keySet()), ", "));
            }
        }
        return requirements;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Requirement<?>> void registerRequirementType(Class<T> rClass) {

        if (!rClass.isAnnotationPresent(RequirementInformation.class)) {
            RaidCraft.LOGGER.warning("Cannot register " + rClass.getCanonicalName() + " as Requirement because it has no Information tag!");
            return;
        }
        for (Constructor<?> constructor : rClass.getDeclaredConstructors()) {
            if (constructor.getParameterTypes()[1].isAssignableFrom(ConfigurationSection.class)) {
                constructor.setAccessible(true);
                constructors.put(rClass, (Constructor<T>) constructor);
                // get the name for aliasing
                String name = StringUtils.formatName(rClass.getAnnotation(RequirementInformation.class).value());
                requirementClasses.put(name, rClass);
                RaidCraft.LOGGER.info("Registered Requirement Type: " + name);
                break;
            }
        }
    }
}

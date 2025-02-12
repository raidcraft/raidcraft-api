package de.raidcraft.api.reward;

import de.raidcraft.RaidCraft;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Philip Urban
 */
@Deprecated
public class RewardManager {

    private static final Map<String, Class<? extends Reward<?>>> rewardClasses = new CaseInsensitiveMap<>();
    private static final Map<Class<? extends Reward<?>>, Constructor<? extends Reward<?>>> constructors = new HashMap<>();

    public static <O> List<Reward<O>> createRewards(ConfigurationSection config) {

        List<Reward<O>> rewards = new ArrayList<>();
        if (config == null || config.getKeys(false) == null) {
            return rewards;
        }

        for (String rewardKey : config.getKeys(false)) {
            ConfigurationSection rewardSection = config.getConfigurationSection(rewardKey);

            String type = rewardSection.getString("type");
            ConfigurationSection args = rewardSection.isConfigurationSection("args")
                    ? rewardSection.getConfigurationSection("args") : new MemoryConfiguration();
            args.set("name", type);

            Class<? extends Reward<?>> rClass = rewardClasses.get(type);
            if (rClass == null) {
                RaidCraft.LOGGER.warning("There are no reward types defined for the type " + type);
                RaidCraft.LOGGER.warning("Available Reward Types are: " + String.join(", ", new ArrayList<>(rewardClasses.keySet())));
                return rewards;
            }
            try {
                final Reward<O> reward = (Reward<O>) constructors.get(rClass).newInstance(rewardSection);
                if (reward instanceof AbstractReward) {
                    ((AbstractReward) reward).load(args);
                }
                rewards.add(reward);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
                e.printStackTrace();
            }
        }
        return rewards;
    }

    public static <T extends Reward<?>> void registerRewardType(Class<T> rClass) {

        if (!rClass.isAnnotationPresent(RewardInformation.class)) {
            RaidCraft.LOGGER.warning("Cannot register " + rClass.getCanonicalName() + " as Reward because it has no Information tag!");
            return;
        }
        for (Constructor<?> constructor : rClass.getDeclaredConstructors()) {
            if (constructor.getParameterTypes()[0].isAssignableFrom(ConfigurationSection.class)) {
                constructor.setAccessible(true);
                constructors.put(rClass, (Constructor<T>) constructor);
                // get the displayName for aliasing
                String name = StringUtils.formatName(rClass.getAnnotation(RewardInformation.class).value());
                rewardClasses.put(name, rClass);
                RaidCraft.info("Registered Reward Type: " + name, "RewardManager");
                break;
            }
        }
    }
}

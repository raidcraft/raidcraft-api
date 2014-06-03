package de.raidcraft.api.action.requirement;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.LocationUtil;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class RequirementFactory implements Component {

    private static final RequirementFactory INSTANCE = new RequirementFactory();
    @NonNull
    public static RequirementFactory getInstance() {

        return INSTANCE;
    }

    private final Map<String, Requirement<?>> requirements = new CaseInsensitiveMap<>();

    private RequirementFactory() {

        RaidCraft.registerComponent(RequirementFactory.class, this);
        registerGlobalRequirements();
    }

    private void registerGlobalRequirements() {

        registerRequirement("player.is-sprinting", Player::isSprinting);
        registerRequirement("player.location", new Requirement<Player>() {
            @Override
            public boolean test(Player player) {

                Location location = player.getLocation();
                ConfigurationSection config = getConfig();
                World world = Bukkit.getWorld(config.getString("world", player.getWorld().getName()));
                if (config.isSet("world") && world == null) return false;

                if (config.isSet("x") && config.isSet("y") && config.isSet("z") && config.isSet("world")) {
                    if (config.isSet("radius")) {
                        return LocationUtil.isWithinRadius(location,
                                new Location(world,
                                        config.getInt("x"),
                                        config.getInt("y"),
                                        config.getInt("z")),
                                config.getInt("radius"));
                    }
                    return config.getInt("x") == location.getBlockX()
                            && config.getInt("y") == location.getBlockY()
                            && config.getInt("z") == location.getBlockZ()
                            && world.equals(location.getWorld());
                }
                return false;
            }
        });
    }

    private <T> void registerRequirement(@NonNull String identifier, @NonNull Requirement<T> requirement) {

        requirements.put(identifier, requirement);
        RaidCraft.LOGGER.info("registered global requirement: " + identifier);
    }

    @SneakyThrows
    public <T> void registerRequirement(@NonNull JavaPlugin plugin, @NonNull String identifier, @NonNull Requirement<T> requirement) {

        identifier = plugin.getName() + "." + identifier;
        if (requirements.containsKey(identifier)) {
            throw new RequirementException("Requirement '" + identifier + "' is already registered!");
        }
        requirements.put(identifier, requirement);
        RaidCraft.LOGGER.info("registered requirement: " + identifier);
    }

    public void unregisterRequirement(@NonNull JavaPlugin plugin, @NonNull String identifier) {

        Requirement<?> requirement = requirements.remove(identifier);
        if (requirement == null) requirement = requirements.remove(plugin.getName() + "." + identifier);
        if (requirement != null) {
            RaidCraft.LOGGER.info("removed requirement: " + identifier + " (" + plugin.getName() + ")");
        }
    }

    public void unregisterRequirements(@NonNull JavaPlugin plugin) {

        requirements.keySet().stream()
                .filter(key -> key.startsWith(plugin.getName().toLowerCase()))
                .forEach(requirements::remove);
        RaidCraft.LOGGER.info("removed all requirements of: " + plugin.getName());
    }

    public Map<String, Requirement<?>> getRequirements() {

        return new HashMap<>(requirements);
    }

    @SneakyThrows
    public Requirement<?> create(@NonNull String identifier, @NonNull ConfigurationSection config) {

        if (!requirements.containsKey(identifier)) {
            throw new RequirementException("unknown requirement: " + identifier);
        }
        return new RequirementConfigWrapper<>(requirements.get(identifier), config);
    }
}
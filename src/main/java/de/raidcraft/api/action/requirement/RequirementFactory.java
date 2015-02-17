package de.raidcraft.api.action.requirement;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.config.builder.ConfigBuilder;
import de.raidcraft.util.CaseInsensitiveMap;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
        ActionAPI.registerGlobalRequirements(this);
    }

    public <T> RequirementFactory registerGlobalRequirement(@NonNull String identifier, @NonNull Requirement<T> requirement) {

        requirements.put(identifier, requirement);
        ConfigBuilder.registerConfigGenerator(requirement);
        RaidCraft.info("registered global requirement: " + identifier
                , "requirement.global");
        return this;
    }

    @SneakyThrows
    public <T> RequirementFactory registerRequirement(@NonNull JavaPlugin plugin, @NonNull String identifier, @NonNull Requirement<T> requirement) {

        identifier = plugin.getName() + "." + identifier;
        if (requirements.containsKey(identifier)) {
            throw new RequirementException("Requirement '" + identifier + "' is already registered!");
        }
        requirements.put(identifier, requirement);
        ConfigBuilder.registerConfigGenerator(requirement);
        RaidCraft.info("registered requirement: " + identifier
                , "requirement." + plugin.getName());
        return this;
    }

    public void unregisterRequirement(@NonNull JavaPlugin plugin, @NonNull String identifier) {

        Requirement<?> requirement = requirements.remove(identifier);
        if (requirement == null) requirement = requirements.remove(plugin.getName() + "." + identifier);
        if (requirement != null) {
            RaidCraft.info("removed requirement: " + identifier + " (" + plugin.getName() + ")"
                    , "requirement." + plugin.getName());
        }
    }

    public void unregisterRequirements(@NonNull JavaPlugin plugin) {

        requirements.keySet().stream()
                .filter(key -> key.startsWith(plugin.getName().toLowerCase()))
                .forEach(requirements::remove);
        RaidCraft.info("removed all requirements of: " + plugin.getName()
                , "requirement." + plugin.getName());
    }

    public Map<String, Requirement<?>> getRequirements() {

        return new HashMap<>(requirements);
    }

    private Requirement<?> create(String id, @NonNull String requirement, @NonNull ConfigurationSection config) throws RequirementException {

        if (!requirements.containsKey(requirement)) {
            throw new RequirementException("unknown requirement: " + requirement);
        }
        RequirementConfigWrapper<?> wrapper = new RequirementConfigWrapper<>(id, requirements.get(requirement), config);
        wrapper.load();
        return wrapper;
    }

    public Collection<Requirement<?>> createRequirements(String id, ConfigurationSection requirements) throws RequirementException {

        ArrayList<Requirement<?>> list = new ArrayList<>();
        if (requirements == null) {
            return list;
        }
        for (String key : requirements.getKeys(false)) {
            list.add(create(id + "." + key, requirements.getString(key + ".type"), requirements.getConfigurationSection(key)));
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public <T> Collection<Requirement<T>> createRequirements(String id, ConfigurationSection requirements, Class<T> type) throws RequirementException {

        return createRequirements(id, requirements).stream()
                .filter(action -> action.matchesType(type))
                .map(action -> (Requirement<T>) action)
                .collect(Collectors.toList());
    }

    public String getRequirementIdentifier(Requirement<?> requirement) {

        for (Map.Entry<String, Requirement<?>> entry : requirements.entrySet()) {
            if (entry.getValue().equals(requirement)) {
                return entry.getKey();
            }
        }
        return "undefined";
    }
}
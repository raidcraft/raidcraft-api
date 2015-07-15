package de.raidcraft.api.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.action.flow.Flow;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.config.builder.ConfigBuilder;
import de.raidcraft.api.config.builder.ConfigGenerator;
import de.raidcraft.util.CaseInsensitiveMap;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Silthus
 */
public final class RequirementFactory<T> {

    @Getter
    private final Class<T> type;
    private final Map<String, Requirement<T>> requirements = new CaseInsensitiveMap<>();
    private final Map<String, ConfigGenerator.Information> requirementInformation = new CaseInsensitiveMap<>();
    // key -> alias, value -> requirement identifier
    private final Map<String, String> requirementAliases = new CaseInsensitiveMap<>();

    protected RequirementFactory(Class<T> type) {

        this.type = type;
    }

    public Optional<ConfigGenerator.Information> getInformation(String identifier) {

        if (requirementInformation.containsKey(identifier)) {
            return Optional.of(requirementInformation.get(identifier));
        }
        if (requirementAliases.containsKey(identifier)) {
            return Optional.ofNullable(requirementInformation.get(requirementAliases.get(identifier)));
        }
        return Optional.empty();
    }

    public RequirementFactory addAlias(String requirement, String alias) {

        requirementAliases.put(alias, requirement);
        return this;
    }

    public RequirementFactory addAlias(BasePlugin plugin, String requirement, String alias) {

        return addAlias(plugin.getName().toLowerCase() + "." + requirement, alias);
    }

    public RequirementFactory registerGlobalRequirement(@NonNull String identifier, @NonNull Requirement<T> requirement) {

        requirements.put(identifier, requirement);
        Optional<ConfigGenerator.Information> information = ConfigBuilder.getInformation(requirement);
        if (information.isPresent()) {
            requirementInformation.put(identifier, information.get());
        } else {
            RaidCraft.LOGGER.warning("no @Information defined for requirement " + identifier);
        }
        RaidCraft.info("registered requirement: " + identifier, "requirement");
        return this;
    }

    public RequirementFactory registerRequirement(@NonNull JavaPlugin plugin, @NonNull String identifier, @NonNull Requirement<T> requirement) {

        identifier = plugin.getName() + "." + identifier;
        return registerGlobalRequirement(identifier, requirement);
    }

    public void unregisterRequirement(@NonNull JavaPlugin plugin, @NonNull String identifier) {

        Requirement<T> requirement = requirements.remove(identifier);
        if (requirement == null) requirement = requirements.remove(plugin.getName() + "." + identifier);
        if (requirement != null) {
            RaidCraft.info("removed requirement: " + identifier + " (" + plugin.getName() + ")", "requirement." + plugin.getName());
        }
    }

    public void unregisterRequirements(@NonNull JavaPlugin plugin) {

        requirements.keySet().stream()
                .filter(key -> key.startsWith(plugin.getName().toLowerCase()))
                .forEach(requirements::remove);
        RaidCraft.info("removed all requirements of: " + plugin.getName()
                , "requirement." + plugin.getName());
    }

    public Map<String, Requirement<T>> getRequirements() {

        return new HashMap<>(requirements);
    }

    public boolean contains(Requirement requirement) {

        return requirements.values().contains(requirement);
    }

    public boolean contains(String actionId) {

        return requirements.keySet().contains(actionId) || requirementAliases.keySet().contains(actionId);
    }

    public Optional<Requirement<T>> create(String id, @NonNull String requirement, @NonNull ConfigurationSection config) {

        if (!requirements.containsKey(requirement)) {
            // lets see if we find a matching alias
            if (requirementAliases.containsKey(id) && requirements.containsKey(requirementAliases.get(id))) {
                id = requirementAliases.get(id);
            } else {
                ActionAPI.UNKNOWN_REQUIREMENTS.add(requirement);
                return Optional.empty();
            }
        }
        RequirementConfigWrapper<T> wrapper = new RequirementConfigWrapper<>(id, requirements.get(requirement), config, getType());
        wrapper.load();
        return Optional.of(wrapper);
    }

    public List<Requirement<T>> createRequirements(String id, ConfigurationSection requirements) {

        ArrayList<Requirement<T>> list = new ArrayList<>();
        if (requirements == null) {
            return list;
        }
        // lets parse via flow first and continue if the key is a list
        list.addAll(Flow.parseRequirements(requirements, getType()));

        for (String key : requirements.getKeys(false)) {
            // handled by flow
            if (requirements.isList(key)) continue;
            Optional<Requirement<T>> optional = create(id + "." + key, requirements.getString(key + ".type"), requirements.getConfigurationSection(key));
            if (optional.isPresent()) {
                list.add(optional.get());
            }
        }
        return list;
    }

    public Optional<String> getRequirementIdentifier(Requirement requirement) {

        for (Map.Entry<String, Requirement<T>> entry : requirements.entrySet()) {
            if (entry.getValue().equals(requirement)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }
}
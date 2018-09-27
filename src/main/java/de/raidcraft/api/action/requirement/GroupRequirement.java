package de.raidcraft.api.action.requirement;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.RequirementConfigWrapper;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@Data
public class GroupRequirement<T> implements ContextualRequirement<T> {

    private final List<Requirement<T>> requirements = new ArrayList<>();

    @Information(
            value = "internal.group",
            desc = "Group requirement for internal use only! Do not use this inside configs."
    )
    @Override
    public boolean test(T type, RequirementConfigWrapper<T> context, ConfigurationSection config) {

        return context.getRequirements().stream()
                .filter(requirement -> ActionAPI.matchesType(requirement, type.getClass()))
                .map(requirement -> (Requirement<T>) requirement)
                .allMatch(requirement -> requirement.test(type));
    }
}

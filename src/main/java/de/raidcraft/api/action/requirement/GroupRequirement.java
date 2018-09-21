package de.raidcraft.api.action.requirement;

import de.raidcraft.api.action.ActionAPI;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@Data
public class GroupRequirement<T> implements Requirement<T> {

    private final List<Requirement<T>> requirements = new ArrayList<>();

    @Information(
            value = "internal.group",
            desc = "Group requirement for internal use only! Do not use this inside configs."
    )
    @Override
    public boolean test(T type, ConfigurationSection config) {

        return requirements.stream()
                .filter(requirement -> ActionAPI.matchesType(requirement, type.getClass()))
                .allMatch(requirement -> requirement.test(type));
    }
}

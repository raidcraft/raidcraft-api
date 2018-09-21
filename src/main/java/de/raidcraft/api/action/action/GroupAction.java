package de.raidcraft.api.action.action;

import de.raidcraft.api.action.ActionAPI;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@Data
public class GroupAction<T> implements Action<T> {

    private final List<Action<T>> actions = new ArrayList<>();

    @Information(
            value = "internal.group",
            desc = "Internally used action to group multiple actions together. Do not use in a config!"
    )
    @Override
    public void accept(T type, ConfigurationSection config) {

        actions.stream()
                .filter(action -> ActionAPI.matchesType(action, type.getClass()))
                .forEach(action -> action.accept(type));
    }
}

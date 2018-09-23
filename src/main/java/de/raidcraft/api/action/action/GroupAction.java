package de.raidcraft.api.action.action;

import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;

@Data
public class GroupAction<T> implements Action<T> {


    @Information(
            value = "internal.group",
            desc = "Internally used action to group multiple actions together. Do not use in a config!"
    )
    @Override
    public void accept(T type, ConfigurationSection config) {
    }
}

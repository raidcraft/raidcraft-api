package de.raidcraft.api.action.flow;

import lombok.Data;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

@Data
public class GlobalFlowParameters {

    private final List<String> worlds;
    private final List<String> regions;

    public GlobalFlowParameters(@NonNull ConfigurationSection config) {
        this.worlds = config.getStringList("worlds");
        this.regions = config.getStringList("regions");
    }
}

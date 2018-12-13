package de.raidcraft.api.random;

import lombok.Data;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

@Data
public class LootTableMeta {

    private String name;
    private String description;
    private Material icon;

    public LootTableMeta(ConfigurationSection config) {
        this.name = config.getString("name");
        this.description = config.getString("desc");
        this.icon = Material.matchMaterial(config.getString("icon"));
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public Optional<Material> getIcon() {
        return Optional.ofNullable(icon);
    }
}

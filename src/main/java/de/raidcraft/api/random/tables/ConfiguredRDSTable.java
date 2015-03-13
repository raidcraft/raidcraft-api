package de.raidcraft.api.random.tables;

import de.raidcraft.api.random.GenericRDSTable;
import de.raidcraft.api.random.RDS;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSObjectFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ConfiguredRDSTable extends GenericRDSTable {

    @RDSObjectFactory.Name("table")
    public static class TableFactory implements RDSObjectFactory {

        @Override
        public RDSObject createInstance(ConfigurationSection config) {

            return RDS.getTable(config.getString("name")).get();
        }
    }

    private final String name;
    private final ConfigurationSection config;

    public ConfiguredRDSTable(String name, ConfigurationSection config) {

        super();
        this.name = name;
        this.config = config;
    }

    public void load() {

        ConfigurationSection entries = getConfig().getConfigurationSection("entries");
        if (entries != null && entries.getKeys(false) != null && !entries.getKeys(false).isEmpty()) {
            for (String key : entries.getKeys(false)) {
                ConfigurationSection section = entries.getConfigurationSection(key);
                Optional<RDSObject> object = RDS.createObject(section.getString("type"), section);
                if (object.isPresent()) {
                    addEntry(object.get());
                }
            }
        }
    }
}

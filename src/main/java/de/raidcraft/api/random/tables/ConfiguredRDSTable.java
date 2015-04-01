package de.raidcraft.api.random.tables;

import de.raidcraft.api.random.GenericRDSTable;
import de.raidcraft.api.random.Loadable;
import de.raidcraft.api.random.RDS;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSObjectFactory;
import de.raidcraft.api.random.RDSTable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ConfiguredRDSTable extends GenericRDSTable implements Loadable {

    @RDSObjectFactory.Name("table")
    public static class TableFactory implements RDSObjectFactory {

        @Override
        public RDSObject createInstance(ConfigurationSection config) {

            Optional<RDSTable> table = RDS.getTable(config.getString("name"));
            if (table.isPresent()) {
                return table.get();
            }
            return new ConfiguredRDSTable(config.getParent());
        }
    }

    private final ConfigurationSection config;

    public ConfiguredRDSTable(ConfigurationSection config) {

        super();
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

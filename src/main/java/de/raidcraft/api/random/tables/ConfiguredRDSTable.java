package de.raidcraft.api.random.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.random.GenericRDSTable;
import de.raidcraft.api.random.Loadable;
import de.raidcraft.api.random.RDS;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSObjectFactory;
import de.raidcraft.api.random.RDSTable;
import de.raidcraft.util.ConfigUtil;
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
            } else if (config.isSet("name")) {
                RaidCraft.LOGGER.info("RDSTable referenced in " + ConfigUtil.getFileName(config)
                        + " " + config.getString("name") + " does not exist!");
            }
            return new ConfiguredRDSTable();
        }
    }

    public void load(ConfigurationSection config) {

        ConfigurationSection entries = config.getConfigurationSection("entries");
        if (entries != null && entries.getKeys(false) != null && !entries.getKeys(false).isEmpty()) {
            for (String key : entries.getKeys(false)) {
                ConfigurationSection section = entries.getConfigurationSection(key);
                if (section != null) {
                    Optional<RDSObject> object = RDS.createObject(section.getString("type", "table"), section);
                    if (object.isPresent()) {
                        addEntry(object.get());
                    } else {
                        RaidCraft.LOGGER.info("RDSObject referenced in " + ConfigUtil.getFileName(config)
                                + " " + section.getString("type") + " does not exist!");
                    }
                } else {
                    RaidCraft.LOGGER.warning("Invalid config section for loot table inside " + ConfigUtil.getFileName(config));
                }
            }
        }
    }
}

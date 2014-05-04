package de.raidcraft.api.ebean;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;

/**
 * @author Silthus
 */
public class DatabaseConfig extends ConfigurationBase<BasePlugin> {

    public DatabaseConfig(BasePlugin plugin) {

        super(plugin, "database.yml");
    }
}

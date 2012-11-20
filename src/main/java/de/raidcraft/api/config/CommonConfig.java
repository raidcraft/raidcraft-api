package de.raidcraft.api.config;

import de.raidcraft.api.BasePlugin;

import java.io.File;

/**
 * @author Silthus
 */
public class CommonConfig extends ConfigurationBase {

    public CommonConfig(BasePlugin plugin, String name) {

        super(plugin, name);
    }

    public CommonConfig(BasePlugin plugin, File file) {

        super(plugin, file);
    }
}

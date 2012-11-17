package de.raidcraft.api.config;

import com.sk89q.rebar.config.ConfigurationException;
import com.sk89q.rebar.config.YamlConfigurationFile;
import com.sk89q.rebar.config.YamlStyle;
import com.sk89q.rebar.config.annotations.Configurator;
import de.raidcraft.api.BasePlugin;
import org.yaml.snakeyaml.DumperOptions;

import java.io.File;
import java.io.IOException;

/**
 * @author Silthus
 */
public class ConfigurationBase extends YamlConfigurationFile {

    public ConfigurationBase(BasePlugin plugin, String name) {

        super(new File(plugin.getDataFolder(), name),
                new YamlStyle(DumperOptions.FlowStyle.BLOCK, 4));
        File file = new File((plugin.getDataFolder()), name);
        // create dir
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        // load the config
        setHeader("###########################################################",
                "#    Raid-Craft Configuration File: " + name,
                "#    Plugin: " + plugin.getName() + " - v" + plugin.getDescription().getVersion(),
                "###########################################################");
        try {
            // lets create some defaults if the file does not exist
            Configurator configurator = new Configurator();
            configurator.registerInstance(this);
            configurator.load(this, this);
            if (!file.exists()) {
                configurator.save(this, this);
            }
            load();
        } catch (IOException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        } catch (ConfigurationException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
    }
}

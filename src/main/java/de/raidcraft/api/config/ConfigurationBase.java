package de.raidcraft.api.config;

import com.sk89q.rebar.config.ConfigurationException;
import com.sk89q.rebar.config.YamlConfigurationFile;
import com.sk89q.rebar.config.YamlStyle;
import com.sk89q.rebar.config.annotations.Configurator;
import com.sk89q.rebar.util.DefaultsUtils;
import de.raidcraft.api.BasePlugin;
import org.yaml.snakeyaml.DumperOptions;

import java.io.File;
import java.io.IOException;

/**
 * @author Silthus
 */
public class ConfigurationBase extends YamlConfigurationFile {

    private final BasePlugin plugin;

    public ConfigurationBase(BasePlugin plugin, String name) {

        super(new File(plugin.getDataFolder(), name),
                new YamlStyle(DumperOptions.FlowStyle.BLOCK, 4));

        this.plugin = plugin;

        // create our own file reference
        File file = new File(plugin.getDataFolder(), name);
        // load the config
        setHeader("###########################################################",
                "#    Raid-Craft Configuration File: " + name,
                "#    Plugin: " + plugin.getName() + " - v" + plugin.getDescription().getVersion(),
                "###########################################################");

        try {
            // create the normal defaults file if it does not exist
            if (!file.exists()) {
                DefaultsUtils.createDefaultConfiguration(this.getClass(), file, "defaults/" + name);
                save();
            }
            // load the config file
            load();
            Configurator configurator = new Configurator();
            configurator.registerInstance(this);
            configurator.load(this, this);
        } catch (IOException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        } catch (ConfigurationException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
    }

    public BasePlugin getPlugin() {

        return plugin;
    }
}

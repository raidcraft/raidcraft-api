package de.raidcraft.api.config;

import com.sk89q.rebar.config.ConfigurationException;
import com.sk89q.rebar.config.ConfigurationNode;
import com.sk89q.rebar.config.YamlConfigurationFile;
import com.sk89q.rebar.config.YamlStyle;
import com.sk89q.rebar.config.annotations.Configurator;
import com.sk89q.rebar.config.annotations.Setting;
import com.sk89q.rebar.util.DefaultsUtils;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import org.yaml.snakeyaml.DumperOptions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author Silthus
 */
public class ConfigurationBase extends YamlConfigurationFile {

    public ConfigurationBase(BasePlugin plugin, String name) {

        super(new File(plugin.getDataFolder(), name),
                new YamlStyle(DumperOptions.FlowStyle.BLOCK, 4));
        // create our own file reference
        File file = new File(plugin.getDataFolder(), name);
        // load the config
        setHeader("###########################################################",
                "#    Raid-Craft Configuration File: " + name,
                "#    Plugin: " + plugin.getName() + " - v" + plugin.getDescription().getVersion(),
                "###########################################################");

        try {
            // lets create some defaults if the file does not exist
            Configurator configurator = new SimpleConfigurator();
            configurator.registerInstance(this);
            // create the normal defaults file if it does not exist
            if (!file.exists()) {
                DefaultsUtils.createDefaultConfiguration(this.getClass(), file, "defaults/" + name);
                load();
                configurator.load(this, this);
                save();
            } else {
                // it is important to load the config first or else it wont use the @Setting annotations
                load();
                configurator.load(this, this);
            }
        } catch (IOException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        } catch (ConfigurationException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
    }

    public class SimpleConfigurator extends Configurator {

        @Override
        protected boolean handleLoad(Field field, Object object, Setting setting, Object value) throws ConfigurationException {

            String path = getSettingPath(field, setting);
            try {
                if (get(path) != null) {
                    value = get(path);
                }
                field.setAccessible(true);
                field.set(object, value);
            } catch (IllegalAccessException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
                e.printStackTrace();
                return true;
            }
            return true;
        }

        @Override
        protected boolean handleSave(ConfigurationNode node, String path, Setting setting, Object value) {

            set(path, value);
            return false;
        }
    }
}

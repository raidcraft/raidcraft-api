package de.raidcraft.api.components;

import com.google.inject.AbstractModule;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.components.loader.ComponentLoader;
import de.raidcraft.api.config.ConfigurationBase;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

/**
 * @author zml2008
 */
@Getter
public abstract class AbstractComponent extends AbstractModule {

    /**
     * The raw configuration for this component. This is usually accessed through
     * ConfigurationBase subclasses and #configure()
     */
    private ConfigurationSection rawConfiguration;

    private BasePlugin plugin;

    private ComponentLoader loader;

    private ComponentInformation info;

    private boolean enabled;

    protected void setUp(BasePlugin plugin, ComponentLoader loader, ComponentInformation info) {
        this.plugin = plugin;
        this.loader = loader;
        this.info = info;
    }

    /**
     * This method is called once all of this Component's fields have been set up
     * and all other Component classes have been discovered
     */
    public abstract void enable();

    public void disable() {}

    @Override
    protected void configure() {}

    public void reload() {
        if (getConfiguration() != null && getConfiguration() instanceof ConfigurationBase) {
            ((ConfigurationBase) getConfiguration()).reload();
        }
    }

    protected <T extends de.raidcraft.api.config.ConfigurationBase> T configure(T config) {
        return getPlugin().configure(config);
    }

    public boolean isEnabled() {
        return enabled;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ComponentLoader getComponentLoader() {
        return loader;
    }

    public ComponentInformation getInformation() {
        return info;
    }

    public ConfigurationSection getConfiguration() {
        if (rawConfiguration != null) {
            return rawConfiguration;
        } else {
            return rawConfiguration = getComponentLoader().getConfiguration(this);
        }
    }

    public void saveConfig() {
        if (getConfiguration() != null && getConfiguration() instanceof ConfigurationBase) {
            ((ConfigurationBase) getConfiguration()).save();
        }
    }

    public abstract Map<String, String> getCommands();
}

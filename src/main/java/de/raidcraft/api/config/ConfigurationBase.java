package de.raidcraft.api.config;

import de.raidcraft.api.BasePlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import static de.raidcraft.api.config.ConfigUtil.prepareSerialization;
import static de.raidcraft.api.config.ConfigUtil.smartCast;

/**
 * @author Silthus
 */
public abstract class ConfigurationBase<T extends BasePlugin> extends YamlConfiguration implements Config {

    /**
     * Refrence to the plugin instance.
     */
    private final T plugin;
    /**
     * The Name of the config file
     */
    private final String name;
    /**
     * The actual physical file object.
     */
    private File file;
    private DataMap override = null;

    public ConfigurationBase(T plugin, File file) {

        this.plugin = plugin;
        this.name = file.getName();
        this.file = file;
    }

    public ConfigurationBase(T plugin, String name) {

        this(plugin, new File(plugin.getDataFolder(), name));
    }

    public void merge(ConfigurationBase config, String path) {

        if (config == null) return;
        getOverrideConfig().merge(config.getOverrideSection(path));
    }

    public void merge(ConfigurationBase config) {

        getOverrideConfig().merge(config);
    }

    public void merge(ConfigurationSection config) {

        getOverrideConfig().merge(config);
    }

    public <V> V getOverride(String key, V def) {

        if (!isSet(key)) {
            set(key, def);
            save();
        }
        if (def instanceof Integer) return (V)(Integer)getOverrideInt(key, (Integer) def);
        if (def instanceof Double) return (V) (Double) getOverrideDouble(key, (Double) def);
        return (V) getOverrideConfig().get(key, def);
    }

    public int getOverrideInt(String key, int def) {

        return getOverrideConfig().getInt(key, def);
    }

    public double getOverrideDouble(String key, double def) {

        return getOverrideConfig().getDouble(key, def);
    }

    public String getOverrideString(String key, String def) {

        return getOverrideConfig().getString(key, def);
    }

    public boolean getOverrideBool(String key, boolean def) {

        return getOverrideConfig().getBoolean(key, def);
    }

    public ConfigurationSection getOverrideSection(String path) {

        return getOverrideConfig().getSafeConfigSection(path);
    }

    public DataMap getOverrideConfig() {

        if (override == null) {
            setOverrideConfig(createDataMap());
        }
        return this.override;
    }

    public void setOverrideConfig(DataMap override) {

        this.override = override;
    }

    public ConfigurationSection getSafeConfigSection(String path) {

        ConfigurationSection configurationSection = getConfigurationSection(path);
        if (configurationSection == null) {
            configurationSection = createSection(path);
        }
        return configurationSection;
    }

    public DataMap createDataMap() {

        return new YamlDataMap(this, this);
    }

    public DataMap createDataMap(String path) {

        return new YamlDataMap(getSafeConfigSection(path), this);
    }

    public File getFile() {

        return file;
    }

    public T getPlugin() {

        return plugin;
    }

    public void reload() {

        load();
    }

    public void save() {

        save(file);
    }

    public void load(boolean annotations) {

        load(file);
        if (annotations) {
            // load the annoations
            loadAnnotations();
        }
    }

    public void load() {

        load(true);
    }

    @Override
    public final void load(File file) {

        try {
            if (!file.exists()) {
                copyFile();
            }
            // load the config by calling the bukkit super method
            super.load(file);
            // plugin.getLogger().info("[" + plugin.getName() + "] loaded config file \"" + name + "\" successfully.");
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAnnotations() {

        loadAnnotations(this);
        save(file);
    }

    private void loadAnnotations(Object object) {

        for (Field field : getFieldsRecur(object.getClass())) {
            field.setAccessible(true);

            try {
                if (field.isAnnotationPresent(ConfigSubClass.class) && field.get(object) != null) {
                    loadAnnotations(field.get(object));
                } else if (field.isAnnotationPresent(Setting.class)) {

                    String key = field.getAnnotation(Setting.class).value();
                    final Object value = smartCast(field.getGenericType(), get(key));

                    if (value != null) {
                        field.set(object, value);
                    } else {
                        set(key, prepareSerialization(field.get(object)));
                    }
                }
            } catch (IllegalAccessException e) {
                plugin.getLogger().log(Level.SEVERE, "Error setting configuration value of field: ", e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public final void save(File file) {

        try {
            saveAnnotations();
            super.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveAnnotations() {

        saveAnnotations(this);
    }

    private void saveAnnotations(Object object) {

        for (Field field : getFieldsRecur(object.getClass())) {
            field.setAccessible(true);
            try {
                if (field.isAnnotationPresent(ConfigSubClass.class) && field.get(object) != null) {
                    saveAnnotations(field.get(object));
                } else if (field.isAnnotationPresent(Setting.class)) {
                    String key = field.getAnnotation(Setting.class).value();
                    set(key, prepareSerialization(field.get(object)));
                }
            } catch (IllegalAccessException e) {
                plugin.getLogger().log(Level.SEVERE, "Error setting configuration value of field: ", e);
                e.printStackTrace();
            }
        }
    }

    private List<Field> getFieldsRecur(Class<?> clazz) {

        return getFieldsRecur(clazz, false);
    }

    private List<Field> getFieldsRecur(Class<?> clazz, boolean includeObject) {

        List<Field> fields = new ArrayList<>();
        while (clazz != null && (includeObject || !Object.class.equals(clazz))) {

            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private void copyFile() {

        try {
            // read the template file from the resources folder
            InputStream stream = plugin.getResource("defaults/" + name);
            if (stream == null) {
                plugin.getLogger().warning("There is no default config for " + name);
                file.createNewFile();
                return;
            }
            OutputStream out = new FileOutputStream(file);
            // buffer 1024 byte so we don't need to write/read too much
            byte[] buf = new byte[1024];
            int len;
            while ((len = stream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            // exit cleanly
            stream.close();
            out.close();
        } catch (IOException iex) {
            plugin.getLogger().log(Level.WARNING, "could not create default config: " + name, iex);
        }
    }

    @Override
    public int getInt(String path, int def) {

        if (!isSet(path)) {
            set(path, def);
            save();
        } else {
            return super.getInt(path, def);
        }
        return getInt(path);
    }

    @Override
    public String getString(String path, String def) {

        if (def == null) return super.getString(path, def);
        if (!isSet(path)) {
            set(path, def);
            save();
        } else {
            return super.getString(path, def);
        }
        return getString(path);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {

        if (!isSet(path)) {
            set(path, def);
            save();
        } else {
            return super.getBoolean(path, def);
        }
        return getBoolean(path);
    }

    @Override
    public double getDouble(String path, double def) {

        if (!isSet(path)) {
            set(path, def);
            save();
        } else {
            return super.getDouble(path, def);
        }
        return getDouble(path);
    }

    @Override
    public long getLong(String path, long def) {

        if (!isSet(path)) {
            set(path, def);
            save();
        } else {
            return super.getLong(path, def);
        }
        return getLong(path);
    }
}

package de.raidcraft.api.config;

import de.raidcraft.api.BasePlugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
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
public abstract class ConfigurationBase extends YamlConfiguration {

    /**
     * Refrence to the plugin instance.
     */
    private final BasePlugin plugin;
    /**
     * The Name of the config file
     */
    private final String name;
    /**
     * The actual physical file object.
     */
    private File file;

    public ConfigurationBase(BasePlugin plugin, String name) {

        this.plugin = plugin;
        this.name = name;
        this.file = new File(plugin.getDataFolder(), name);
        // set the header
        options().header("###########################################################\n" +
                "#    Raid-Craft Configuration File: " + name + "\n" +
                "#    Plugin: " + plugin.getName() + " - v" + plugin.getDescription().getVersion() + "\n" +
                "###########################################################");
        options().copyHeader(true);
        load(file);
    }

    public void reload() {

        load(file);
    }

    @Override
    public final void load(File file) {

        if (!file.exists()) {
            copyFile();
        }
        try {
            // load the config by calling the bukkit super method
            super.load(file);
            // load the annoations
            loadAnnotations();
            plugin.getLogger().info("[" + plugin.getName() + "] loaded config file \"" + name + "\" successfully.");
            return;
        } catch (IOException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
        plugin.getLogger().warning("[" + plugin.getName() + "] error when loading config file \"" + name + "\"");
    }

    private void loadAnnotations() {

        for (Field field : getFieldsRecur(getClass())) {
            if (!field.isAnnotationPresent(Setting.class)) continue;
            String key = field.getAnnotation(Setting.class).value();
            final Object value = smartCast(field.getGenericType(), get(key));
            try {
                field.setAccessible(true);
                if (value != null) {
                    field.set(this, value);
                } else {
                    set(key, prepareSerialization(field.get(this)));
                }
            } catch (IllegalAccessException e) {
                plugin.getLogger().log(Level.SEVERE, "Error setting configuration value of field: ", e);
                e.printStackTrace();
            }
        }
        save(file);
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

        for (Field field : getFieldsRecur(getClass())) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(Setting.class)) continue;
            String key = field.getAnnotation(Setting.class).value();
            try {
                set(key, prepareSerialization(field.get(this)));
            } catch (IllegalAccessException e) {
                plugin.getLogger().log(Level.SEVERE, "Error getting configuration value of field: ", e);
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
                plugin.getLogger().warning("There is not default config for " + name);
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
}

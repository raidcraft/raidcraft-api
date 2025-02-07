package de.raidcraft.util;

import org.bukkit.configuration.ConfigurationSection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 * @deprecated please use the {@link de.raidcraft.api.config.DataMap}
 */
@Deprecated
public class DataMap implements Map<String, Object> {

    protected Map<String, Object> data = new LinkedHashMap<>();

    public DataMap() {

    }

    public DataMap(Map<String, Object> map) {

        this.data = map;
    }

    @SuppressWarnings("unchecked")
    public DataMap(ResultSet resultSet, String columnKey, String columnValue) throws SQLException {

        if (!resultSet.first()) return;
        if (columnKey == null || columnValue == null) return;
        // go thru all the given keys
        do {
            String key = resultSet.getString(columnKey);
            String value = resultSet.getString(columnValue);
            // if the key contains a dot we need to create a map for it
            if (key.contains("\\.")) {
                String[] split = key.split("\\.");
                if (!data.containsKey(split[0])) {
                    data.put(split[0], new DataMap());
                }
                DataMap map = (DataMap) data.get(split[0]);
                map.put(split[1], value);
            } else if (data.containsKey(key)) {
                // if the key exists twice or more we need to make a list for it
                if (!(data.get(key) instanceof List)) {
                    String tmpVal = (String) data.get(key);
                    data.put(key, new ArrayList<String>());
                    ((List<String>) data.get(key)).add(tmpVal);
                }
                ((List<String>) data.get(key)).add(value);
            } else {
                // add a normal key:value reference
                data.put(key, value);
            }
        } while (resultSet.next());
    }

    public DataMap(ConfigurationSection config, String... exclude) {

        Set<String> excludedKeys = new HashSet<>(Arrays.asList(exclude));
        // go tru all keys in the given config section
        for (String key : config.getKeys(false)) {
            if (!excludedKeys.contains(key)) {
                // get the keys and desolve them
                if (config.isList(key)) {
                    // this will add the list to the map
                    data.put(key, config.getStringList(key));
                } else if (config.isConfigurationSection(key)) {
                    // if the subpart is a config section it has sub keys
                    data.put(key, new DataMap());
                    // in our case that means a map because we only go one down
                    DataMap map = (DataMap) data.get(key);
                    // go thru all keys of the sub section and add their respective values
                    for (String child : config.getConfigurationSection(key).getKeys(false)) {
                        map.put(child, config.getConfigurationSection(key).get(child));
                    }
                } else {
                    data.put(key, config.get(key));
                }
            }
        }
    }

    public DataMap getDataMap(String key) {

        if (data.get(key) == null || !(data.get(key) instanceof DataMap)) {
            return new DataMap();
        }
        return (DataMap) data.get(key);
    }

    public Set<String> getKeys() {

        return data.keySet();
    }

    public void set(String key, Object value) {

        data.put(key, value);
    }

    public String getString(String key) {

        return getString(key, "");
    }

    public String getString(String key, String def) {

        if (data.get(key) == null) {
            return def;
        }
        if (data.get(key) instanceof Integer || data.get(key) instanceof Double) {
            return data.get(key) + "";
        }
        return (String) data.get(key);
    }

    public int getInt(String key) {

        return getInt(key, 0);
    }

    public int getInt(String key, int def) {

        if (data.get(key) == null) {
            return def;
        }
        try {
            return Integer.parseInt((String) data.get(key));
        } catch (Exception e) {
            return (Integer) data.get(key);
        }
    }

    public double getDouble(String key) {

        return getDouble(key, 0.0);
    }

    public double getDouble(String key, double def) {

        if (data.get(key) == null) {
            return def;
        }
        try {
            return Double.parseDouble((String) data.get(key));
        } catch (Exception e) {
            return (Double) data.get(key);
        }
    }

    public boolean getBoolean(String key) {

        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean def) {

        if (data.get(key) == null) {
            return def;
        }
        return (Boolean) data.get(key);
    }

    @SuppressWarnings("unchecked")
    public List<String> getStringList(String key) {

        LinkedList<String> list = new LinkedList<>();
        if (data.get(key) == null) {
            return list;
        }
        if (data.get(key) instanceof String) {
            list.add((String) data.get(key));
            return list;
        }
        return (List<String>) data.get(key);
    }

    public boolean isSet(String key) {

        return containsKey(key);
    }

    @Override
    public int size() {

        return data.size();
    }

    @Override
    public boolean isEmpty() {

        return data.size() < 1;
    }

    @Override
    public boolean containsKey(Object key) {

        return data.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {

        return data.containsValue(value);
    }

    @Override
    public Object get(Object key) {

        return data.get(key);
    }

    @Override
    public Object put(String key, Object value) {

        return data.put(key, value);
    }

    @Override
    public Object remove(Object key) {

        return data.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {

        data.putAll(m);
    }

    @Override
    public void clear() {

        data.clear();
    }

    @Override
    public Set<String> keySet() {

        return data.keySet();
    }

    @Override
    public Collection<Object> values() {

        return data.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {

        return data.entrySet();
    }
}

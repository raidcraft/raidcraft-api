package de.raidcraft.util;

import java.util.HashMap;

/**
 * @author Silthus
 */
public class CaseInsensitiveMap<V> extends HashMap<String, V> {

    @Override
    public V put(String key, V value) {

        return super.put(key.toLowerCase(), value);
    }

    @Override
    public V get(Object key) {

        return super.get(key == null ? null : key.toString().toLowerCase());
    }

    @Override
    public boolean containsKey(Object key) {

        return super.containsKey(key == null ? null : key.toString().toLowerCase());
    }

    @Override
    public V remove(Object key) {

        return super.remove(key == null ? null : key.toString().toLowerCase());
    }
}

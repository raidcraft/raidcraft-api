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

    // not @Override because that would require the key parameter to be of type Object
    public V get(String key) {
        return super.get(key.toLowerCase());
    }
}

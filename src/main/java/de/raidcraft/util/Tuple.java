package de.raidcraft.util;

import lombok.Data;

@Data
public final class Tuple<K, V> {

    private final K key;
    private final V value;
}

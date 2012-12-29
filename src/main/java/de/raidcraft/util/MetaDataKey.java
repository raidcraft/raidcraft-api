package de.raidcraft.util;

/**
 * @author Silthus
 */
public enum MetaDataKey {

    PLAYER_PLACED_BLOCK("rc_player_placed_block", boolean.class);

    private final String key;
    private final Class<?> type;

    private MetaDataKey(String key, Class<?> type) {

        this.key = key;
        this.type = type;
    }

    public String getKey() {

        return key;
    }

    public Class<?> getType() {

        return type;
    }
}

package de.raidcraft.api.items;

/**
 * @author mdoering
 */
public interface Gem extends AttributeHolder {

    int getId();

    String getName();

    GemColor getColor();
}

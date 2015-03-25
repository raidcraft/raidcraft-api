package de.raidcraft.api.items;

/**
 * @author mdoering
 */
public interface Gem extends AttributeHolder {

    public int getId();

    public String getName();

    public GemColor getColor();
}

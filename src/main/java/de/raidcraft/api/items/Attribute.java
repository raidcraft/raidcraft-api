package de.raidcraft.api.items;

/**
 * @author Silthus
 */
public class Attribute {

    private final String name;
    private final int value;

    public Attribute(String name, int value) {

        this.name = name;
        this.value = value;
    }

    public String getName() {

        return name;
    }

    public int getValue() {

        return value;
    }
}

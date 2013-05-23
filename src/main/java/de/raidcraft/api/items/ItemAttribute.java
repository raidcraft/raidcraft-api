package de.raidcraft.api.items;

/**
 * @author Silthus
 */
public class ItemAttribute implements Comparable<ItemAttribute> {

    private final AttributeType type;
    private final int value;

    public ItemAttribute(AttributeType type, int value) {

        this.type = type;
        this.value = value;
    }

    public AttributeType getType() {

        return type;
    }

    public String getName() {

        return type.name();
    }

    public String getDisplayName() {

        return type.getGermanName();
    }

    public int getValue() {

        return value;
    }

    @Override
    public int compareTo(ItemAttribute o) {

        return getDisplayName().compareTo(o.getDisplayName());
    }
}

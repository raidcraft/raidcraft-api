package de.raidcraft.api.items;

import de.raidcraft.util.StringUtils;

/**
 * @author Silthus
 */
public class ItemAttribute implements Comparable<ItemAttribute> {

    private final String name;
    private final String displayName;
    private final int value;

    public ItemAttribute(String name, String displayName, int value) {

        this.name = StringUtils.formatName(name);
        this.displayName = displayName;
        this.value = value;
    }

    public String getName() {

        return name;
    }

    public String getDisplayName() {

        return displayName;
    }

    public int getValue() {

        return value;
    }

    @Override
    public int compareTo(ItemAttribute o) {

        return getDisplayName().compareTo(o.getDisplayName());
    }
}

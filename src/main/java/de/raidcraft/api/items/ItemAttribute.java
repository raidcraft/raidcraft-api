package de.raidcraft.api.items;

import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class ItemAttribute implements Comparable<ItemAttribute>, Cloneable {

    private final AttributeType type;
    private final int value;
    private String itemLine;

    public ItemAttribute(AttributeType type, int value) {

        this.type = type;
        this.value = value;
        String str = ChatColor.GREEN + "+";
        if (getValue() < 0) {
            str = ChatColor.RED + "-";
        }
        str += getValue() + " " + getDisplayName();
        itemLine = str;
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

    public String getItemLine() {

        return itemLine;
    }

    public int getValue() {

        return value;
    }

    public void setItemLine(String itemLine) {

        this.itemLine = itemLine;
    }

    @Override
    public ItemAttribute clone() throws CloneNotSupportedException {

        super.clone();
        ItemAttribute attribute = new ItemAttribute(getType(), getValue());
        attribute.setItemLine(getItemLine());
        return attribute;
    }

    @Override
    public int compareTo(ItemAttribute o) {

        return getDisplayName().compareTo(o.getDisplayName());
    }

    @Override
    public String toString() {

        return getItemLine();
    }
}

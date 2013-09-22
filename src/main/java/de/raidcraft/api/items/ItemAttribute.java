package de.raidcraft.api.items;

import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class ItemAttribute implements Comparable<ItemAttribute>, Cloneable {

    private final AttributeType type;
    private final int value;
    private int equipedValue;
    private String itemLine;

    public ItemAttribute(AttributeType type, int value) {

        this.type = type;
        this.value = value;
        rebuildItemLine();
    }

    private void rebuildItemLine() {

        ChatColor color = ChatColor.WHITE;
        if (value < equipedValue) {
            color = ChatColor.RED;
        } else if (value > equipedValue) {
            color = ChatColor.GREEN;
        }
        String str = color + "+";
        if (getValue() < 0) {
            str = color + "-";
        }
        str += getValue() + " " + getDisplayName();
        if (value != equipedValue) {
            if (value < equipedValue) {
                str += color + " [" + (value - equipedValue) + "]";
            } else {
                str += color + " [+" + (value - equipedValue) + "]";
            }
        }
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

    public void setEquipedValue(int equipedValue) {

        this.equipedValue = equipedValue;
        rebuildItemLine();
    }

    public int getEquipedValue() {

        return equipedValue;
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

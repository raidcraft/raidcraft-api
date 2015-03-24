package de.raidcraft.api.items;

import com.avaje.ebean.annotation.EnumValue;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public enum ItemBindType {

    SOULBOUND("Seelengebunden", ChatColor.DARK_PURPLE),
    @EnumValue("BOE")
    BIND_ON_EQUIP("Wird beim Anlegen gebunden", ChatColor.AQUA),
    @EnumValue("BOP")
    BIND_ON_PICKUP("Wird beim Aufheben gebunden", ChatColor.BLUE),
    @EnumValue("QUEST")
    QUEST("Questgegenstand", ChatColor.GOLD),
    @EnumValue("NONE")
    NONE("", ChatColor.WHITE);

    private final String itemText;
    private final ChatColor color;

    private ItemBindType(String itemText, ChatColor color) {

        this.itemText = itemText;
        this.color = color;
    }

    public String getItemText() {

        return itemText;
    }

    public ChatColor getColor() {

        return color;
    }

    public static ItemBindType fromString(String name) {

        if (name == null || name.equals("")) return NONE;
        name = ChatColor.stripColor(name).trim();
        for (ItemBindType type : values()) {
            if (type.name().equalsIgnoreCase(name)
                    || type.getItemText().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}

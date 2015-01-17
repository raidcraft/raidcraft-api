package de.raidcraft.api.items;

import com.avaje.ebean.annotation.EnumValue;

/**
 * @author Silthus
 */
public enum ItemBindType {

    SOULBOUND("Seelengebunden"),
    @EnumValue("BOE")
    BIND_ON_EQUIP("Wird beim Anlegen gebunden"),
    @EnumValue("BOP")
    BIND_ON_PICKUP("Wird beim Aufheben gebunden"),
    @EnumValue("QUEST")
    QUEST("Questgegenstand"),
    @EnumValue("NONE")
    NONE("");

    private final String itemText;

    private ItemBindType(String itemText) {

        this.itemText = itemText;
    }

    public String getItemText() {

        return itemText;
    }
}

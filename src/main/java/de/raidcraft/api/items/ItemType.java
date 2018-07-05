package de.raidcraft.api.items;

import com.avaje.ebean.annotation.EnumValue;
import de.raidcraft.util.EnumUtils;

/**
 * @author Silthus
 */
public enum ItemType {

    @EnumValue("WEAPON")
    WEAPON("Waffe"),
    @EnumValue("ARMOR")
    ARMOR("Rüstung"),
    @EnumValue("USEABLE")
    USEABLE("Benutzbar"),
    @EnumValue("EQUIPMENT")
    EQUIPMENT("Equipment"),
    @EnumValue("QUEST")
    QUEST("Quest Item"),
    @EnumValue("ENCHANTMENT")
    ENCHANTMENT("Verzauberung"),
    @EnumValue("GEM")
    GEM("Edelstein"),
    @EnumValue("ENHANCEMENT")
    ENHANCEMENT("Verbesserung"),
    @EnumValue("CRAFTING")
    CRAFTING("Handwerkswaren"),
    @EnumValue("CONSUMEABLE")
    CONSUMEABLE("Verbrauchbar"),
    @EnumValue("TRASH")
    TRASH("Plunder"),
    @EnumValue("SPECIAL")
    SPECIAL("SPECIAL"),
    @EnumValue("PROFESSION")
    PROFESSION("PROFESSION"),
    @EnumValue("CLASS")
    CLASS("CLASS"),
    @EnumValue("UNDEFINED")
    UNDEFINED("Undefined");


    private final String germanName;

    ItemType(String germanName) {

        this.germanName = germanName;
    }

    public String getGermanName() {

        return germanName;
    }

    public static ItemType fromGermanName(String name) {

        name = name.toLowerCase();
        for (ItemType type : values()) {
            if (type.getGermanName().toLowerCase().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public static ItemType fromString(String str) {

        return EnumUtils.getEnumFromString(ItemType.class, str);
    }
}

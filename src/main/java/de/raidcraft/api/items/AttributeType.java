package de.raidcraft.api.items;

import com.avaje.ebean.annotation.EnumValue;
import de.raidcraft.util.EnumUtils;

/**
 * @author Silthus
 */
public enum AttributeType {

    @EnumValue("STRENGTH")
    STRENGTH("Stärke"),
    @EnumValue("AGILITY")
    AGILITY("Beweglichtkeit"),
    @EnumValue("STAMINA")
    STAMINA("Ausdauer"),
    @EnumValue("INTELLECT")
    INTELLECT("Intelligenz"),
    @EnumValue("SPIRIT")
    SPIRIT("Willenskraft");

    private final String germanName;

    private AttributeType(String germanName) {

        this.germanName = germanName;
    }

    public String getGermanName() {

        return germanName;
    }

    public static AttributeType fromGermanName(String name) {

        name = name.toLowerCase();
        for (AttributeType type : values()) {
            if (type.getGermanName().toLowerCase().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public static AttributeType fromString(String str) {

        return EnumUtils.getEnumFromString(AttributeType.class, str);
    }
}

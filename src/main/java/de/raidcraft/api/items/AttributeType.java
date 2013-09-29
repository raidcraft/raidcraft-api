package de.raidcraft.api.items;

import com.avaje.ebean.annotation.EnumValue;
import de.raidcraft.util.EnumUtils;

/**
 * @author Silthus
 */
public enum AttributeType {

    @EnumValue("STRENGTH")
    STRENGTH("Stärke", 1.0),
    @EnumValue("AGILITY")
    AGILITY("Beweglichtkeit", 1.0),
    @EnumValue("STAMINA")
    STAMINA("Ausdauer", 1.0),
    @EnumValue("INTELLECT")
    INTELLECT("Intelligenz", 1.0),
    @EnumValue("SPIRIT")
    SPIRIT("Willenskraft", 1.0),
    @EnumValue("CRITICAL_STRIKE")
    CRITICAL_STRIKE("Kritische Trefferwertung", 1.0),
    @EnumValue("HIT")
    HIT("Trefferwertung", 1.0),
    @EnumValue("MAGICAL_HIT")
    MAGICAL_HIT("Magische Trefferwertung", 1.0),
    @EnumValue("ATTACK_POWER")
    ATTACK_POWER("Angriffskraft", 0.5),
    @EnumValue("MAGIC_DAMAGE")
    MAGIC_DAMAGE("Zauberschaden", 0.86),
    @EnumValue("EVADE")
    EVADE("Ausweichen", 1.0),
    @EnumValue("PARRY")
    PARRY("Parieren", 1.0),
    @EnumValue("BLOCK")
    BLOCK("Block Wert", 0.6),
    @EnumValue("DEFENSE")
    DEFENSE("Verteidigung", 1.0),
    @EnumValue("HEAL")
    HEAL("Bonus Heilung", 0.45),
    @EnumValue("HASTE")
    HASTE("Hast", 1.0),
    @EnumValue("ARMOR_PENETRATION")
    ARMOR_PENETRATION("Rüstungsdurchschlag", 1.0);

    private final String germanName;
    private final double itemLevelValue;

    private AttributeType(String germanName, double itemLevelValue) {

        this.germanName = germanName;
        this.itemLevelValue = itemLevelValue;
    }

    public String getGermanName() {

        return germanName;
    }

    public double getItemLevelValue() {

        return itemLevelValue;
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

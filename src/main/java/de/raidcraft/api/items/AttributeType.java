package de.raidcraft.api.items;

import com.avaje.ebean.annotation.EnumValue;
import de.raidcraft.util.EnumUtils;

/**
 * @author Silthus
 */
public enum AttributeType {

    @EnumValue("STRENGTH")
    STRENGTH("Stärke", AttributeDisplayType.INLINE, 1.0),
    @EnumValue("AGILITY")
    AGILITY("Beweglichtkeit", AttributeDisplayType.INLINE, 1.0),
    @EnumValue("STAMINA")
    STAMINA("Ausdauer", AttributeDisplayType.INLINE, 1.0),
    @EnumValue("INTELLECT")
    INTELLECT("Intelligenz", AttributeDisplayType.INLINE, 1.0),
    @EnumValue("SPIRIT")
    SPIRIT("Willenskraft", AttributeDisplayType.INLINE, 1.0),
    @EnumValue("CRITICAL_STRIKE")
    CRITICAL_STRIKE("Kritische Trefferwertung", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("HIT")
    HIT("Trefferwertung", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("MAGICAL_HIT")
    MAGICAL_HIT("Magische Trefferwertung", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("ATTACK_POWER")
    ATTACK_POWER("Angriffskraft", AttributeDisplayType.BELOW, 0.5),
    @EnumValue("SPELL_POWER")
    SPELL_POWER("Zauberschaden", AttributeDisplayType.BELOW, 0.86),
    @EnumValue("EVADE")
    EVADE("Ausweichen", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("PARRY")
    PARRY("Parieren", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("BLOCK")
    BLOCK("Block Wert", AttributeDisplayType.BELOW, 0.6),
    @EnumValue("DEFENSE")
    DEFENSE("Verteidigung", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("HEAL")
    HEAL("Bonus Heilung", AttributeDisplayType.BELOW, 0.45),
    @EnumValue("HASTE")
    HASTE("Hast", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("ARMOR_PENETRATION")
    ARMOR_PENETRATION("Rüstungsdurchschlag", AttributeDisplayType.BELOW, 1.0);

    private final String germanName;
    private final double itemLevelValue;
    private final AttributeDisplayType displayType;

    private AttributeType(String germanName, AttributeDisplayType displayType, double itemLevelValue) {

        this.germanName = germanName;
        this.itemLevelValue = itemLevelValue;
        this.displayType = displayType;
    }

    public String getGermanName() {

        return germanName;
    }

    public double getItemLevelValue() {

        return itemLevelValue;
    }

    public AttributeDisplayType getDisplayType() {

        return displayType;
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

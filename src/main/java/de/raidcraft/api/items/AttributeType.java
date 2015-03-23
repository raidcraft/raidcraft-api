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
    @EnumValue("CRITICAL_STRIKE_RATING")
    CRITICAL_STRIKE_RATING("Kritische Trefferwertung", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("HIT_RATING")
    HIT_RATING("Trefferwertung", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("MAGICAL_HIT")
    MAGICAL_HIT("Magische Trefferwertung", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("ATTACK_POWER")
    ATTACK_POWER("Angriffskraft", AttributeDisplayType.BELOW, 0.5),
    @EnumValue("SPELL_POWER")
    SPELL_POWER("Zauberschaden", AttributeDisplayType.BELOW, 0.86),
    @EnumValue("DODGE_RATING")
    DODGE_RATING("Ausweichen", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("PARRY_RATING")
    PARRY_RATING("Parieren", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("SHIELD_BLOCK_RATING")
    SHIELD_BLOCK_RATING("Block Wert", AttributeDisplayType.BELOW, 0.6),
    @EnumValue("DEFENSE_RATING")
    DEFENSE_RATING("Verteidigung", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("HEAL")
    HEAL("Bonus Heilung", AttributeDisplayType.BELOW, 0.45),
    @EnumValue("HASTE_RATING")
    HASTE_RATING("Hast", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("ARMOR_PENETRATION")
    ARMOR_PENETRATION("Rüstungsdurchschlag", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("WEAPON_SKILL_RATING")
    WEAPON_SKILL_RATING("Waffenfähigkeit", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("RANGED_CRITICAL_STRIKE_RATING")
    RANGED_CRITICAL_STRIKE_RATING("Kritische Fernkampf Trefferwertung", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("EXPERTISE_RATING_2")
    EXPERTISE_RATING_2("Waffenkunde", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("EXPERTISE_RATING")
    EXPERTISE_RATING("Waffenkunde", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("RESILIENCE_RATING")
    RESILIENCE_RATING("RESILIENCE_RATING", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("RANGED_ATTACK_POWER")
    RANGED_ATTACK_POWER("Fernkampf Angriffskraft", AttributeDisplayType.BELOW, 0.5),
    @EnumValue("MANA_REGENERATION")
    MANA_REGENERATION("Mana Regeneration", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("ARMOR_PENETRATION_RATING")
    ARMOR_PENETRATION_RATING("Rüstungsdurchschlag", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("HEALTH_REGEN")
    HEALTH_REGEN("Lebensregeneration", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("SPELL_PENETRATION")
    SPELL_PENETRATION("Zauberdurchschlag", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("BLOCK_VALUE")
    BLOCK_VALUE("Blocken", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("MASTERY_RATING")
    MASTERY_RATING("Meisterschaft", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("FIRE_RESISTANCE")
    FIRE_RESISTANCE("Feuer Resistenz", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("FROST_RESISTANCE")
    FROST_RESISTANCE("Frost Resistenz", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("HOLY_RESISTANCE")
    HOLY_RESISTANCE("Heilig Resistenz", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("SHADOW_RESISTANCE")
    SHADOW_RESISTANCE("Schatten Resistenz", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("NATURE_RESISTANCE")
    NATURE_RESISTANCE("Natur Resistenz", AttributeDisplayType.BELOW, 1.0),
    @EnumValue("ARCANE_RESISTANCE")
    ARCANE_RESISTANCE("Arkan Resistenz", AttributeDisplayType.BELOW, 1.0);

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

        AttributeType type = EnumUtils.getEnumFromString(AttributeType.class, str);
        if (type == null) {
            return fromGermanName(str);
        }
        return type;
    }
}

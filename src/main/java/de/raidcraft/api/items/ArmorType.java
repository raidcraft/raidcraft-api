package de.raidcraft.api.items;

import de.raidcraft.util.EnumUtils;
import io.ebean.annotation.EnumValue;

/**
 * @author Silthus
 */
public enum ArmorType {

    @EnumValue("CLOTH")
    CLOTH("Stoff") {
        @Override
        public double getArmorModifier(ItemQuality quality, int itemLevel) {

            switch (quality) {
                case POOR:
                    return itemLevel * 2 + 10;
                case COMMON:
                    return itemLevel * 2 + 11;
                case UNCOMMON:
                    return itemLevel * 1.19 + 5.1;
                case RARE:
                    return (itemLevel * 1.19 + 5.1) * 1.1;
                case EPIC:
                    return (itemLevel * 1.19 + 5.1) * 1.375;
                default:
                    return itemLevel;
            }
        }
    },
    @EnumValue("LEATHER")
    LEATHER("Leder") {
        @Override
        public double getArmorModifier(ItemQuality quality, int itemLevel) {

            switch (quality) {
                case UNCOMMON:
                    return itemLevel * 2.22 + 10.0;
                case RARE:
                    return (itemLevel * 2.22 + 10.0) * 1.1;
                case EPIC:
                    return ((itemLevel * 2.22 + 10.0) * 1.1) * 1.25;
                default:
                    return itemLevel;
            }
        }
    },
    @EnumValue("MAIL")
    MAIL("Kette") {
        @Override
        public double getArmorModifier(ItemQuality quality, int itemLevel) {

            switch (quality) {
                case UNCOMMON:
                    return itemLevel * 4.49 + 29.0;
                case RARE:
                    return (itemLevel * 4.49 + 29.0) * 1.1;
                case EPIC:
                    return ((itemLevel * 4.49 + 29.0) * 1.1) * 1.25;
                default:
                    return itemLevel;
            }
        }
    },
    @EnumValue("PLATE")
    PLATE("Platte") {
        @Override
        public double getArmorModifier(ItemQuality quality, int itemLevel) {

            switch (quality) {
                case UNCOMMON:
                    return itemLevel * 9 + 23.0;
                case RARE:
                    return (itemLevel * 9 + 23.0) * 1.1;
                case EPIC:
                    return ((itemLevel * 9 + 23.0) * 1.1) * 1.25;
                default:
                    return itemLevel;
            }
        }
    },
    @EnumValue("SHIELD")
    SHIELD("Schild") {
        @Override
        public double getArmorModifier(ItemQuality quality, int itemLevel) {

            switch (quality) {
                case UNCOMMON:
                    return (itemLevel * 85) / 3 + 133.0;
                case RARE:
                    return ((itemLevel * 85) / 3 + 133.0) * 1.22;
                case EPIC:
                    return (((itemLevel * 85) / 3 + 133.0) * 1.22) * 1.28;
                default:
                    return itemLevel;
            }
        }
    };

    private final String germanName;

    private ArmorType(String germanName) {

        this.germanName = germanName;
    }

    public String getGermanName() {

        return germanName;
    }

    public abstract double getArmorModifier(ItemQuality quality, int itemLevel);

    public EquipmentSlot getEquipmentSlot(int itemId) {

        return EquipmentSlot.fromItemId(itemId);
    }

    public static ArmorType fromGermanName(String name) {

        name = name.toLowerCase();
        for (ArmorType type : values()) {
            if (type.getGermanName().toLowerCase().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public static ArmorType fromString(String str) {

        return EnumUtils.getEnumFromString(ArmorType.class, str);
    }
}

package de.raidcraft.api.language;

import de.raidcraft.util.EnumUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Silthus
 */
public enum Language {

    deDE("de_DE"),
    enUS("en_US");

    private final String languageString;

    private Language(String languageString) {

        this.languageString = languageString;
    }

    public String getLanguageString() {

        return languageString;
    }

    public static Language fromString(String lang) {

        for (Language language : values()) {
            if (lang.equalsIgnoreCase(language.getLanguageString())) {
                return language;
            }
        }
        return EnumUtils.getEnumFromString(Language.class, lang);
    }

    public static Language getLanguage(Player player) {

        try {
            Object ep = getMethod("getHandle", player.getClass()).invoke(player, (Object[]) null);
            Field f = ep.getClass().getDeclaredField("locale");
            f.setAccessible(true);
            return fromString((String) f.get(ep));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            return enUS;
        }
    }

    private static Method getMethod(String name, Class<?> clazz) {
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equals(name))
                return m;
        }
        return null;
    }
}

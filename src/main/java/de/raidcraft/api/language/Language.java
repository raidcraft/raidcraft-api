package de.raidcraft.api.language;

import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Silthus
 */
public class Language {

    public static final Language DEFAULT_LANGUAGE = new Language("enUS");

    private final String languageString;

    private Language(String languageString) {

        this.languageString = languageString;
    }

    public String getLanguageString() {

        return languageString;
    }

    public static Language fromString(String lang) {

        return new Language(lang);
    }

    public static Language getLanguage(Player player) {

        try {
            Object ep = getMethod("getHandle", player.getClass()).invoke(player, (Object[]) null);
            Field f = ep.getClass().getDeclaredField("locale");
            f.setAccessible(true);
            return fromString((String) f.get(ep));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            return DEFAULT_LANGUAGE;
        }
    }

    private static Method getMethod(String name, Class<?> clazz) {

        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }
}

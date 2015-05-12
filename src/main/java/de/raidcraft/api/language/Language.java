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
            Object craftBukkitHandle = player.getClass().getDeclaredMethod("getHandle").invoke(player);
            Field locale = craftBukkitHandle.getClass().getDeclaredField("locale");
            locale.setAccessible(true);
            return fromString((String) locale.get(craftBukkitHandle));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            return DEFAULT_LANGUAGE;
        }
    }
}

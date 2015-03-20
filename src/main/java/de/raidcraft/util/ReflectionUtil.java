package de.raidcraft.util;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;

/**
 * @author Silthus
 */
public class ReflectionUtil {

    public static Class<?> getNmsClass(String basePackage, String clazzName) {

        return getNmsClass(basePackage, "", clazzName);
    }

    public static Class<?> getNmsClass(String basePackage, String detailPackage, String clazzName) {

        try {
            String mcVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            Class<?> clazz = Class.forName(basePackage + "." + mcVersion + "." + detailPackage
                    + (detailPackage == null || detailPackage.equals("") ? "" : ".") + clazzName);
            return clazz;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getPrivateField(String fieldName, Class clazz, Object object) {

        Field field;
        Object o = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            o = field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }
}
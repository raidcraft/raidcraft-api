package de.raidcraft.util;

import org.bukkit.Bukkit;

/**
 * @author Silthus
 */
public class ReflectionUtil {

    public static Class<?> getNmsClass(String basePackage, String clazzName) {
        
        return getNmsClass(basePackage, "", clazzName);
    }

    public static Class<?> getNmsClass(String basePackage, String detailPackage, String clazzName) {
        
        String mcVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        Class<?> clazz = Class.forName(basePackage + "." + mcVersion + "." + detailPackage 
                + (detailPackage == null || detailPackage.equals("") ? "" : ".") + clazzName);
        return clazz;
    }
}
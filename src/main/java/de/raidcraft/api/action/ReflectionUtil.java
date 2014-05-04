package de.raidcraft.api.action;

import de.raidcraft.RaidCraft;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author mdoering
 */
public class ReflectionUtil {

    public static boolean isMatchingGenericMethodType(Method method, Class<?> matchingType) {

        for (Type type : method.getGenericParameterTypes()) {
            RaidCraft.LOGGER.info("matching " + type.getTypeName() + " against " + matchingType.getTypeName());
            if (type.getClass().isAssignableFrom(matchingType)) return true;
        }
        return false;
    }
}

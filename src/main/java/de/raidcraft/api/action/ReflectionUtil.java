package de.raidcraft.api.action;

import de.raidcraft.RaidCraft;

import java.lang.reflect.Method;

/**
 * @author mdoering
 */
public class ReflectionUtil {

    public static boolean isMatchingGenericMethodType(Method method, Class<?> matchingType) {

        RaidCraft.LOGGER.info("matching method types");
        for (Class<?> type : method.getParameterTypes()) {
            RaidCraft.LOGGER.info("matching " + type.getTypeName() + " against " + matchingType.getTypeName());
            if (type.getClass().isAssignableFrom(matchingType)) return true;
        }
        return false;
    }
}

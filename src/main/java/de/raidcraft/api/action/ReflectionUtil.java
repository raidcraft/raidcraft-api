package de.raidcraft.api.action;

import java.lang.reflect.Method;

/**
 * @author mdoering
 */
public class ReflectionUtil {

    public static boolean isMatchingGenericMethodType(Method method, Class<?> matchingType) {

        // TODO fix this
        return true;
/*        RaidCraft.LOGGER.info("matching method types");
        for (Type type : method.getGenericParameterTypes()) {
            RaidCraft.LOGGER.info("matching " + type.getClass().getName() + " against " + matchingType.getTypeName());
            if (type.getClass().isAssignableFrom(matchingType)) return true;
        }
        return false;*/
    }
}

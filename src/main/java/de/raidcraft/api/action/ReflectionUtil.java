package de.raidcraft.api.action;

import de.raidcraft.RaidCraft;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author mdoering
 */
public class ReflectionUtil {

    public static boolean genericClassMatchesType(Class<?> clazzToMatch, Class<?> matchingType) {

        RaidCraft.LOGGER.info("matching clazz type: " + clazzToMatch + " against " + matchingType);
        Type[] genericInterfaces = clazzToMatch.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            RaidCraft.LOGGER.info("matching " + genericInterface.getTypeName());
            if (genericInterface instanceof ParameterizedType) {
                Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
                for (Type genericType : genericTypes) {
                    RaidCraft.LOGGER.info(genericType.getTypeName());
                    if (genericType.getClass().isAssignableFrom(matchingType)) {
                        RaidCraft.LOGGER.info("success!");
                        return true;
                    }
                }
            }
        }
        RaidCraft.LOGGER.warning("class match failed!");
        return false;
    }
}

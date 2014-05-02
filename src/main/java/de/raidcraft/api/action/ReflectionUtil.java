package de.raidcraft.api.action;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author mdoering
 */
public class ReflectionUtil {

    public static boolean genericClassMatchesType(Class<?> clazzToMatch, Class<?> matchingType) {

        Type[] genericInterfaces = clazzToMatch.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
                for (Type genericType : genericTypes) {
                    if (genericType.getClass().isAssignableFrom(matchingType)) return true;
                }
            }
        }
        return false;
    }
}

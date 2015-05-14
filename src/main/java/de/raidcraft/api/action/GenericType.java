package de.raidcraft.api.action;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * @author mdoering
 */
public interface GenericType<T> {

    @SuppressWarnings("unchecked")
    public default Optional<Class<T>> getType() {

        Type[] types = de.raidcraft.util.ReflectionUtil.getParameterizedTypes(this);
        if (types != null && types.length > 0) {
            try {
                return Optional.ofNullable((Class<T>) de.raidcraft.util.ReflectionUtil.getClass(types[0]));
            } catch (ClassNotFoundException ignored) {
            }
        }
        return Optional.ofNullable((Class<T>) de.raidcraft.util.ReflectionUtil.findSubClassParameterType(this, GenericType.class, 0));
    }

    public default boolean matchesType(Class<?> entity) {

        Optional<Class<T>> type = getType();
        return entity != null && type.isPresent() && type.get().isAssignableFrom(entity);
    }

    public default boolean matchesType(Object object) {

        return matchesType(object.getClass());
    }
}

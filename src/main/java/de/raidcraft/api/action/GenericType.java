package de.raidcraft.api.action;

import de.raidcraft.util.ReflectionUtil;

import java.util.Optional;

/**
 * @author mdoering
 */
public interface GenericType<T> {

    @SuppressWarnings("unchecked")
    public default Optional<Class<T>> getType() {

        Class<T> type = (Class<T>) ReflectionUtil.findSubClassParameterType(this, GenericType.class, 0);
        return Optional.ofNullable(type);
    }

    public default boolean matchesType(Class<?> entity) {

        Optional<Class<T>> type = getType();
        return entity != null && type.isPresent() && type.get().isAssignableFrom(entity);
    }

    public default boolean matchesType(Object object) {

        return matchesType(object.getClass());
    }
}

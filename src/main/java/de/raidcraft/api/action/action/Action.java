package de.raidcraft.api.action.action;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.util.ReflectionUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Silthus
 */
@FunctionalInterface
public interface Action<T> extends ActionConfigGenerator {

    static Action<?> ofMethod(Object object, String methodName, Object... args) {

        return (type, config) -> {
            try {
                Method method = ReflectionUtil.getMethod(object, methodName, args);
                method.invoke(args);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        };
    }

    static <T extends Action<?>> T of(Class<T> actionClass) {

        for (Method method : actionClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Information.class)) {
                return ActionAPI.createAction(actionClass);
            }
        }
        throw new UnsupportedOperationException("Action " + actionClass.getCanonicalName() + " has no @Information tag!");
    }

    default String getIdentifier() {

        return ActionAPI.getIdentifier(this);
    }

    default void addRequirement(Requirement<T> requirement) {

        throw new UnsupportedOperationException();
    }

    void accept(T type, ConfigurationSection config);

    default void accept(T type) {

        accept(type, new MemoryConfiguration());
    }

    default Action<T> with(String key, Object value) {

        throw new UnsupportedOperationException();
    }

    default Action<T> withArgs(String key, Object value) {

        throw new UnsupportedOperationException();
    }
}

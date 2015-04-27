package de.raidcraft.api.config.builder;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.util.CaseInsensitiveMap;
import lombok.Data;
import org.bukkit.event.Listener;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

/**
 * @author mdoering
 */
@Data
public class ConfigBuilder {

    private static final Map<String, ConfigGenerator.Information> GENERATOR_INFORMATIONS = new CaseInsensitiveMap<>();

    public static void registerInformation(ConfigGenerator generator) {

        Optional<ConfigGenerator.Information> info = getInformation(generator);
        if (info.isPresent()) {
            GENERATOR_INFORMATIONS.put(info.get().value(), info.get());
        }
    }

    public static Optional<ConfigGenerator.Information> getInformation(String identifier) {

        return Optional.ofNullable(GENERATOR_INFORMATIONS.get(identifier));
    }

    public static Optional<ConfigGenerator.Information> getInformation(ConfigGenerator generator) {

        for (Method method : generator.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(ConfigGenerator.Information.class)) {
                return Optional.of(method.getAnnotation(ConfigGenerator.Information.class));
            }
        }
        return Optional.empty();
    }
}

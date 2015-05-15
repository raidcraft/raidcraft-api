package de.raidcraft.api.random;

import org.bukkit.configuration.ConfigurationSection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mdoering
 */
public interface RDSObjectFactory {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Name {

        String value();
    }

    RDSObject createInstance(ConfigurationSection config);
}

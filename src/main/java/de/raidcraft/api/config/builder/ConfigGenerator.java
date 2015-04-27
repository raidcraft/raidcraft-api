package de.raidcraft.api.config.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mdoering
 */
public interface ConfigGenerator {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Information {

        String value();

        String desc();

        String[] conf() default {};
    }
}
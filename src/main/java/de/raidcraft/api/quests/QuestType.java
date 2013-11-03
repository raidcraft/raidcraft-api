package de.raidcraft.api.quests;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Silthus
 */
public interface QuestType {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Name {

        public String value();
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Method {

        public String name();

        public String desc() default "";

        public Type type();
    }

    public enum Type {

        ACTION,
        REQUIREMENT,
        TRIGGER
    }
}

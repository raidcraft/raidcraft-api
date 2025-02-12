package de.raidcraft.api.components;

/**
 * A way for components to register deps on other things
 */
public @interface Depend {
    public Class<?>[] components() default {};
    public String[] plugins() default {};
}

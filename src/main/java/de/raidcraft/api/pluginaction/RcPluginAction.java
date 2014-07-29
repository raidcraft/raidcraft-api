package de.raidcraft.api.pluginaction;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Each Listener must have this Annotation at all
 * action methods. Like Bukkit each method has one parameter:
 * a subclass from PluginAction
 *
 * @author Dragonfire
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RcPluginAction {
}

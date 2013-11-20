package de.raidcraft.api.quests;

import de.raidcraft.api.quests.quest.trigger.Trigger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Silthus
 */
public abstract class QuestTrigger {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Name {

        public String value();
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Method {

        public String value();

        public String desc() default "";
    }

    private final Trigger trigger;

    protected QuestTrigger(Trigger trigger) {

        this.trigger = trigger;
    }

    public Trigger getTrigger() {

        return trigger;
    }

    public String getName() {

        return getTrigger().getName();
    }

    protected abstract void load(ConfigurationSection data);

    protected final void inform(String action, Player player) {

        if (getName().endsWith(action)) {
            trigger.trigger(Quests.getQuestHolder(player));
        }
    }

    /**
     * Here all event handler should be unregister to avoid duplication problems
     */
    public abstract void unregister();
}

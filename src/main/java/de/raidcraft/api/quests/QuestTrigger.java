package de.raidcraft.api.quests;

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

    private String name;

    public String getName() {

        return name;
    }

    protected void setName(String name) {

        this.name = name;
    }

    protected abstract void load(ConfigurationSection data);

    protected final void inform(Player player) {

        Quests.callTrigger(this, player);
    }

    protected final void inform(String subTrigger, Player player) {

        Quests.callTrigger(getName() + "." + subTrigger, player);
    }
}

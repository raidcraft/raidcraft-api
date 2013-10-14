package de.raidcraft.api.quests;

import de.raidcraft.api.quests.quest.QuestTemplate;
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
    }

    private final QuestTemplate questTemplate;
    private final String name;

    protected QuestTrigger(QuestTemplate questTemplate, String name) {

        this.questTemplate = questTemplate;
        this.name = name;
    }

    public QuestTemplate getQuestTemplate() {

        return questTemplate;
    }

    public String getName() {

        return name;
    }

    protected abstract void load(ConfigurationSection data);

    protected final void inform(Player player) {

        Quests.callTrigger(this, player);
    }
}

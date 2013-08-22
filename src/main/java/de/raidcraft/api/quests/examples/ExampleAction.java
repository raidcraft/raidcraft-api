package de.raidcraft.api.quests.examples;

import de.raidcraft.api.quests.QuestType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
@QuestType.Name("example")
public class ExampleAction implements QuestType {

    // TODO: register the Action via Quests.registerQuestType(Plugin, this)

    @Method(name = "add", type = Type.ACTION)
    public static void addExp(Player player, ConfigurationSection data) {
        // do stuff
    }

    @Method(name = "level", type = Type.REQUIREMENT)
    public static boolean isLevel(Player player, ConfigurationSection data) {
        // check stuff
        return true;
    }
}

package de.raidcraft.api.quests;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
@QuestType.Name("example")
public class ExampleAction implements QuestType {

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

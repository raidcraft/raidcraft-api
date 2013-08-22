package de.raidcraft.api.quests.examples;

import de.raidcraft.api.quests.QuestTrigger;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * @author Silthus
 */
@QuestTrigger.Name("example")
public class ExampleTrigger extends QuestTrigger implements Listener {

    // TODO: register the trigger via Quests.registerTrigger(Plugin, Class<?>)

    private int x;
    private int y;
    private int z;

    @Override
    protected void load(ConfigurationSection data) {

        x = data.getInt("x");
        y = data.getInt("y");
        z = data.getInt("z");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {

        Location to = event.getTo();
        if (to.getBlockX() == x && to.getBlockY() == y && to.getBlockZ() == z) {
            inform(event.getPlayer());
        }
    }
}

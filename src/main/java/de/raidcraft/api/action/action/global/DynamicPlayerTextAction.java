package de.raidcraft.api.action.action.global;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.ActionConfigWrapper;
import de.raidcraft.api.action.action.ContextualAction;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class DynamicPlayerTextAction implements ContextualAction<Player>, Listener {

    @Information(
            value = "text.dynamic",
            desc = "Displays the player a clickable text that will execute all actions after clicked.",
            conf = {
                    "text: the clickable text that is displayed to the player"
            }
    )
    @Override
    public void accept(Player player, ActionConfigWrapper<Player> context, ConfigurationSection config) {

        context.setExecuteChildActions(false);
        RaidCraft.getComponent(DynamicPlayerTextManager.class).registerPlayerAction(player, context, config);
    }
}

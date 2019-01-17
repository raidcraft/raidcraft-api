package de.raidcraft.api.action.action.global;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.tags.TPlayerTag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class RemovePlayerTag implements Action<Player> {

    @Information(
            value = "player.tag.remove",
            aliases = {"tag.remove"},
            desc = "Removes the given player tag from the database.",
            conf = {
                    "tag: tag id to remove"
            }
    )
    @Override
    public void accept(Player player, ConfigurationSection config) {

        TPlayerTag.findTag(player.getUniqueId(), config.getString("tag"))
                .ifPresent(TPlayerTag::delete);
    }
}

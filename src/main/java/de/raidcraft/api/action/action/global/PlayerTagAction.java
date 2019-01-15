package de.raidcraft.api.action.action.global;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.tags.TPlayerTag;
import de.raidcraft.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PlayerTagAction implements Action<Player> {

    @Information(
            value = "player.tag",
            aliases = {"tag"},
            desc = "Tags the player with the given tag and optional tag expiration. Will override any existing tag.",
            conf = {
                    "tag: id of the tag",
                    "duration: 1y10d2h33m2s1 -> 1 year 10 days 2 hours 33 minutes 2 seconds 1 tick (optional -> no expiration)"
            }
    )
    @Override
    public void accept(Player player, ConfigurationSection config) {

        if (!config.isSet("tag")) {
            RaidCraft.LOGGER.warning("No Tag specified in player.tag action: " + ConfigUtil.getFileName(config));
            return;
        }

        TPlayerTag.createTag(player, config.getString("tag"), config.getString("duration"));
    }
}

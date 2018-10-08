package de.raidcraft.api.action.requirement.global;

import com.google.common.base.Strings;
import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.requirement.tables.TPlayerTag;
import de.raidcraft.util.TimeUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class TagRequirement implements Requirement<Player> {

    @Information(
            value = "player.tag",
            aliases = {"tag"},
            desc = "Checks if the player has the given tag.",
            conf = {
                    "tag: id of the tag",
                    "ignore-duration: ignores the duration and only checks the existance (default: false)"
            }
    )
    @Override
    public boolean test(Player player, ConfigurationSection config) {

        return TPlayerTag.findTag(player.getUniqueId(), config.getString("tag")).map(tag -> {
            if (!Strings.isNullOrEmpty(tag.getDuration()) && !config.getBoolean("ignore-duration", false)) {
                // check if the tag has expired
                return tag.getWhenModified().plusMillis(TimeUtil.parseTimeAsMillis(tag.getDuration())).isAfter(Instant.now());
            }
            return true;
        }).orElse(false);
    }
}

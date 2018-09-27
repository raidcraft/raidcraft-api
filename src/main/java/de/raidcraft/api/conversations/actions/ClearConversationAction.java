package de.raidcraft.api.conversations.actions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ClearConversationAction implements Action<Player> {

    @Information(
            value = "conversation.clear",
            aliases = {"conv.clear"},
            desc = "Clears all set conversation on the given host.",
            conf = {
                    "host: host to clear player conversations from"
            }
    )
    @Override
    public void accept(Player player, ConfigurationSection config) {

        Conversations.getConversationHost(config.getString("host")).ifPresentOrElse(
                host -> host.clearConversation(player),
                () -> RaidCraft.LOGGER.warning("Invalid host " + config.getString("host") + " in " + ConfigUtil.getFileName(config))
        );

    }
}

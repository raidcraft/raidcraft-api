package de.raidcraft.api.conversations.actions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

public class UnsetConversationAction implements Action<Player> {

    @Information(
            value = "conversation.unset",
            aliases = {"conv.unset"},
            desc = "Unsets the conversation from the host.",
            conf = {
                    "conv: conversation to remove",
                    "host: host to clear conversation from"
            }
    )
    @Override
    public void accept(Player player, ConfigurationSection config) {

        Optional<ConversationTemplate> conversationTemplate = Conversations.getConversationTemplate(config.getString("conv"));
        if (!conversationTemplate.isPresent()) {
            RaidCraft.LOGGER.warning("Invalid conversation " + config.getString("conf") + " in " + ConfigUtil.getFileName(config));
            return;
        }
        Optional<ConversationHost<?>> conversationHost = Conversations.getConversationHost(config.getString("host"));
        conversationHost.ifPresent(host -> host.unsetConversation(player, conversationTemplate.get()));
        if (!conversationHost.isPresent()) {
            RaidCraft.LOGGER.warning("Invalid host " + config.getString("host") + " in " + ConfigUtil.getFileName(config));
        }
    }
}

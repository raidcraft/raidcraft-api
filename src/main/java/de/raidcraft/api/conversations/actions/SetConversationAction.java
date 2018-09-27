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

public class SetConversationAction implements Action<Player> {

    @Information(
            value = "conversation.set",
            aliases = {"conv.set"},
            desc = "Sets a given conversation on the given host for the player.",
            conf = {
                    "conv: conversation to set",
                    "host: host to set conversation to"
            }
    )
    @Override
    public void accept(Player player, ConfigurationSection config) {

        Optional<ConversationTemplate> conversationTemplate = Conversations.getConversationTemplate(config.getString("conv"));
        if (!conversationTemplate.isPresent()) {
            RaidCraft.LOGGER.warning("Invalid conversation " + config.getString("conv") + " in " + ConfigUtil.getFileName(config));
            return;
        }
        Optional<ConversationHost<?>> conversationHost = Conversations.getConversationHost(config.getString("host"));
        if (!conversationHost.isPresent()) {
            RaidCraft.LOGGER.warning("Invalid host " + config.getString("host") + " in " + ConfigUtil.getFileName(config));
            return;
        }
        conversationHost.get().setConversation(player, conversationTemplate.get());
    }
}

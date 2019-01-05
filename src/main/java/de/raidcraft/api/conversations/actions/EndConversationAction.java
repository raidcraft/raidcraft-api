package de.raidcraft.api.conversations.actions;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author mdoering
 */
public class EndConversationAction implements Action<Player> {

    @Override
    @Information(
            value = "end",
            aliases = {"conversation.end", "conv.end"},
            desc = "Ends the current conversation with the given or custom reason.",
            conf = "reason: ConversationEndReason or Custom Message"
    )
    public void accept(Player player, ConfigurationSection config) {

        Optional<Conversation> activeConversation = Conversations.getActiveConversation(player);
        activeConversation.ifPresent(conversation -> {
            String reason = config.getString("reason");
            ConversationEndReason endReason;
            if (reason == null) {
                endReason = ConversationEndReason.ENDED;
            } else {
                endReason = ConversationEndReason.fromString(reason);
                if (endReason == null) {
                    endReason = ConversationEndReason.CUSTOM;
                    endReason.setMessage(reason);
                }
            }
            conversation.end(endReason);
        });

    }
}

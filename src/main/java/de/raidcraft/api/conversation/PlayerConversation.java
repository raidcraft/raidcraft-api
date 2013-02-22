package de.raidcraft.api.conversation;

import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class PlayerConversation extends AbstractRunningConversation<Player> {

    public PlayerConversation(Conversation conversation, Player conversationPartner) {

        super(conversation, conversationPartner);
    }
}

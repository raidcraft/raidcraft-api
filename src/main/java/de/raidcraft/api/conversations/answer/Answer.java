package de.raidcraft.api.conversations.answer;

import de.raidcraft.api.action.action.ActionHolder;
import de.raidcraft.api.conversations.conversation.Conversation;
import org.bukkit.ChatColor;

/**
 * @author mdoering
 */
public interface Answer extends ActionHolder {

    String DEFAULT_ANSWER_TEMPLATE = "default";

    /**
     * Gets the text displayed as answer.
     *
     * @return displayed answer
     */
    String getText();

    /**
     * Gets the color that should format the text.
     *
     * @return color of the text, default: {@link ChatColor#YELLOW}
     */
    ChatColor getColor();

    /**
     * Sets the given ChatColor as text color of this answer.
     *
     * @param color to set
     */
    void setColor(ChatColor color);

    /**
     * Executes all actions of this answer for the given conversation.
     *
     * @param conversation to execute actions for
     */
    void executeActions(Conversation conversation);
}

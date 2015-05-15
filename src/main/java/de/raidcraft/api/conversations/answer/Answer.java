package de.raidcraft.api.conversations.answer;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ActionHolder;
import de.raidcraft.api.conversations.conversation.Conversation;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author mdoering
 */
public interface Answer extends ActionHolder {

    String DEFAULT_ANSWER_TEMPLATE = "default";

    /**
     * Gets an optional formatted message that will be clickable.
     *
     * @return displayed answer
     */
    default Optional<FancyMessage> getMessage() {

        return Optional.empty();
    }

    Answer message(FancyMessage message);

    /**
     * Gets the text displayed as answer.
     *
     * @return displayed answer
     */
    String getText();

    /**
     * Sets the text of the answer.
     *
     * @param text to display
     * @return this answer
     */
    Answer text(String text);

    /**
     * Gets the chat color the answer should be displayed with.
     *
     * @return color of the answer
     */
    ChatColor getColor();

    /**
     * Sets the given ChatColor as text color of this answer.
     *
     * @param color to set
     */
    Answer color(ChatColor color);

    /**
     * Adds a Conversation action to this answer that is executed when the answer is chosen.
     *
     * @param conversationAction to add
     * @return this answer
     */
    Answer addConversationAction(Action<Conversation> conversationAction);

    /**
     * Adds a Player action to this answer that is executed when the answer is chosen.
     *
     * @param playerAction to add
     * @return this answer
     */
    Answer addPlayerAction(Action<Player> playerAction);

    /**
     * Executes all actions of this answer for the given conversation.
     *
     * @param conversation to execute actions for
     */
    void executeActions(Conversation conversation);
}

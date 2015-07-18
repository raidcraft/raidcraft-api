package de.raidcraft.api.conversations.answer;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ActionHolder;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.requirement.RequirementHolder;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.conversation.Conversation;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author mdoering
 */
public interface Answer extends ActionHolder, RequirementHolder {

    static Answer of(String text, Action... actions) {

        return Conversations.createAnswer(text, actions);
    }

    static <T extends Answer> Optional<Answer> of(Class<T> answerClass, Action... actions) {

        return Conversations.createAnswer(answerClass, actions);
    }

    String DEFAULT_ANSWER_TEMPLATE = "default";
    String DEFAULT_INPUT_TYPE = "input";

    String getType();

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
    Optional<String> getText();

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
     * Trys to add the given requirement to the answer. If the requirement is not a conversation or
     * player requirement it will not be added and silently fail.
     *
     * @param requirement to add to the answer
     * @return this answer
     */
    Answer addRequirement(Requirement<?> requirement);

    /**
     * Adds a Conversation requirement to this answer that is checked before actions are executed.
     *
     * @param conversationRequirement to add
     * @return this answer
     */
    Answer addConversationRequirement(Requirement<Conversation> conversationRequirement);

    /**
     * Adds a Player action to this answer that is executed before actions are executed.
     *
     * @param playerRequirement to add
     * @return this answer
     */
    Answer addPlayerRequirement(Requirement<Player> playerRequirement);

    /**
     * Adds an action to the answer that will be executed when the answer is chosen.
     * The action will only be added if it is a conversation or player action, otherwise
     * it will silently fail.
     *
     * @param action to add
     * @return this answer
     */
    Answer addAction(Action<?> action);

    /**
     * Adds all actions to this answer.
     * @see #addAction(Action)
     *
     * @param actions to add
     * @return this answer
     */
    default Answer addActions(Action<?>... actions) {

        Arrays.stream(actions).forEach(this::addAction);
        return this;
    }

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

    /**
     * Processes the player input and takes actions accordingly.
     *
     * @param conversation to process
     * @param input to process
     * @return true if the answer processed the input and feels responsible to take actions
     */
    boolean processInput(Conversation conversation, String input);

    /**
     * Aborts the execution of all currently running actions.
     */
    void abortActionExecution();
}

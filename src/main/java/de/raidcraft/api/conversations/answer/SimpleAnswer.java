package de.raidcraft.api.conversations.answer;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.util.fanciful.FancyMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = false, of = {"type", "text", "actions", "requirements", "message"})
public class SimpleAnswer implements Answer {

    private final String type;
    private final List<Action<?>> actions;
    private final List<Requirement<?>> requirements;
    private Optional<FancyMessage> message = Optional.empty();
    private Optional<String> text;
    private ChatColor color = ChatColor.GOLD;
    private boolean abortActions = false;

    public SimpleAnswer(String type, String text, List<Action<?>> actions, List<Requirement<?>> requirements) {

        this.type = type;
        this.text = Optional.ofNullable(text);
        this.actions = actions == null ? new ArrayList<>() : actions;
        this.requirements = requirements == null ? new ArrayList<>() : requirements;
    }

    public SimpleAnswer(String text) {

        this(Answer.DEFAULT_ANSWER_TEMPLATE, text, null, null);
    }

    public SimpleAnswer(String type, FancyMessage message, List<Action<?>> actions, List<Requirement<?>> requirements) {

        this.type = type;
        this.message = Optional.of(message);
        this.actions = actions == null ? new ArrayList<>() : actions;
        this.requirements = requirements == null ? new ArrayList<>() : requirements;
    }

    public SimpleAnswer(FancyMessage message) {

        this(Answer.DEFAULT_ANSWER_TEMPLATE, message, null, null);
    }

    @Override
    public void abortActionExecution() {

        abortActions = true;
    }

    @Override
    public Answer message(FancyMessage message) {

        this.message = Optional.of(message);
        return this;
    }

    @Override
    public Answer text(String text) {

        this.text = Optional.ofNullable(text);
        return this;
    }

    @Override
    public Answer color(ChatColor color) {

        this.color = color;
        return this;
    }

    @Override
    public <T> Requirement<T> addRequirement(Requirement<T> requirement) {

        this.requirements.add(requirement);
        return requirement;
    }

    @Override
    public Answer addConversationRequirement(Requirement<Conversation> conversationRequirement) {

        requirements.add(conversationRequirement);
        return this;
    }

    @Override
    public Answer addPlayerRequirement(Requirement<Player> playerRequirement) {

        requirements.add(playerRequirement);
        return this;
    }

    @Override
    public <T> Answer addActionToAnswer(Action<T> action) {

        this.actions.add(action);
        return this;
    }

    @Override
    public Answer addConversationAction(Action<Conversation> conversationAction) {

        actions.add(conversationAction);
        return this;
    }

    @Override
    public Answer addPlayerAction(Action<Player> playerAction) {

        actions.add(playerAction);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void executeActions(Conversation conversation) {

        abortActions = false;
        for (Action<?> action : getActions()) {
            if (abortActions) break;
            if (ActionAPI.matchesType(action, Player.class)) {
                ((Action<Player>) action).accept(conversation.getOwner());
            } else if (ActionAPI.matchesType(action, Conversation.class)) {
                ((Action<Conversation>) action).withPlayer(conversation.getOwner()).accept(conversation);
            } else {
                action.accept(null);
            }
        }
    }

    @Override
    public boolean processInput(Conversation conversation, String input) {

        return false;
    }
}

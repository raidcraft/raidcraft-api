package de.raidcraft.api.conversations.answer;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.conversation.Conversation;
import lombok.Data;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author mdoering
 */
@Data
public class SimpleAnswer implements Answer {

    private final List<Action<?>> actions;
    private Optional<FancyMessage> message = Optional.empty();
    private String text;
    private ChatColor color;

    public SimpleAnswer(String text, List<Action<?>> actions) {

        this.text = text;
        this.actions = actions == null ? new ArrayList<>() : actions;
    }

    public SimpleAnswer(String text) {

        this(text, null);
    }

    public SimpleAnswer(FancyMessage message, List<Action<?>> actions) {

        this.message = Optional.of(message);
        this.actions = actions == null ? new ArrayList<>() : actions;
    }

    public SimpleAnswer(FancyMessage message) {

        this(message, null);
    }

    @Override
    public Answer message(FancyMessage message) {

        this.message = Optional.of(message);
        return this;
    }

    @Override
    public Answer text(String text) {

        this.text = text;
        return this;
    }

    @Override
    public Answer color(ChatColor color) {

        this.color = color;
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
    public void executeActions(Conversation conversation) {

        getActions(Conversation.class).forEach(action -> action.accept(conversation));
        getActions(Player.class).forEach(action -> action.accept(conversation.getEntity()));
    }
}

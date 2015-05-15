package de.raidcraft.api.conversations.answer;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.conversation.Conversation;
import lombok.Data;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mdoering
 */
@Data
public class SimpleAnswer implements Answer {

    private final List<Action<?>> actions;
    private FancyMessage message;
    private String text;
    private ChatColor color;

    public SimpleAnswer(String text, List<Action<?>> actions) {

        this.text = text;
        this.actions = actions == null ? new ArrayList<>() : actions;
    }

    public SimpleAnswer(String text) {

        this(text, null);
    }

    @Override
    public Answer text(String text) {

        this.text = text;
        this.message = new FancyMessage(getText()).color(getColor());
        return this;
    }

    @Override
    public Answer color(ChatColor color) {

        this.color = color;
        text(getText());
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

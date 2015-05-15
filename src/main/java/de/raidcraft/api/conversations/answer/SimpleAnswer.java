package de.raidcraft.api.conversations.answer;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.conversation.Conversation;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mdoering
 */
@Data
public class SimpleAnswer implements Answer {

    private final String text;
    private final List<Action<?>> actions;
    private ChatColor color;

    public SimpleAnswer(String text, List<Action<?>> actions) {

        this.text = text;
        this.actions = actions == null ? new ArrayList<>() : actions;
    }

    public SimpleAnswer(String text) {

        this(text, null);
    }

    @Override
    public void executeActions(Conversation conversation) {

        getActions(Conversation.class).forEach(action -> action.accept(conversation));
        getActions(Player.class).forEach(action -> action.accept(conversation.getEntity()));
    }
}

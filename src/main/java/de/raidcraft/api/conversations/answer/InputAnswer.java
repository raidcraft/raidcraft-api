package de.raidcraft.api.conversations.answer;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.conversations.conversation.Conversation;
import de.raidcraft.util.fanciful.FancyMessage;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author mdoering
 */
public class InputAnswer extends ConfiguredAnswer {

    private String varName = "input";
    @Setter
    @Getter
    private Consumer<String> inputListener = null;

    public InputAnswer(String type, ConfigurationSection config) {

        super(type, config);
        this.varName = config.getString("var", "input");
    }

    public InputAnswer(String type, String text, List<Action<?>> actions, List<Requirement<?>> requirements) {
        super(type, text, actions, requirements);
    }

    public InputAnswer(String text) {
        super(text);
    }

    public InputAnswer(String type, FancyMessage message, List<Action<?>> actions, List<Requirement<?>> requirements) {
        super(type, message, actions, requirements);
    }

    public InputAnswer(FancyMessage message) {
        super(message);
    }

    public InputAnswer setInputListener(Consumer<String> inputListener) {
        this.inputListener = inputListener;
        return this;
    }

    @Override
    protected void load(ConfigurationSection args) {

    }

    @Override
    public boolean processInput(Conversation conversation, String input) {

        conversation.set(varName, input);
        conversation.setLastInput(input);
        if (getInputListener() != null) getInputListener().accept(input);
        return true;
    }
}

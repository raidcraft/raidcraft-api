package de.raidcraft.api.action.flow.types;

import de.raidcraft.api.action.flow.FlowExpression;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.stage.StageTemplate;
import lombok.Data;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.Optional;

/**
 * @author mdoering
 */
@Data
public class FlowAnswer implements FlowExpression {

    private final String text;
    private final Optional<String> inputVariable;

    public FlowAnswer(String text, String inputVar) {

        this.text = text;
        this.inputVariable = Optional.ofNullable(inputVar);
    }

    public FlowAnswer(String text) {

        this(text, null);
    }

    public Optional<Answer> create(StageTemplate template) {

        MemoryConfiguration config = new MemoryConfiguration();
        config.set("withText", getText());
        if (getInputVariable().isPresent()) {
            config.set("var", getInputVariable().get());
            config.set("type", Answer.DEFAULT_INPUT_TYPE);
        }
        return Conversations.getAnswer(template, config);
    }
}

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
    private final String inputVariable;
    private boolean checkingRequirement = false;
    private boolean negate = false;

    public FlowAnswer(String text, String inputVar) {

        this.text = text;
        this.inputVariable = inputVar;
    }

    public FlowAnswer(String text) {

        this(text, null);
    }

    public Optional<String> getInputVariable() {
        return Optional.ofNullable(this.inputVariable);
    }

    public Optional<Answer> create(StageTemplate template) {

        MemoryConfiguration config = new MemoryConfiguration();
        config.set("text", getText());
        getInputVariable().ifPresent(var -> {
            config.set("var", var);
            config.set("type", Answer.DEFAULT_INPUT_TYPE);
        });
        return Conversations.getAnswer(template, config);
    }
}

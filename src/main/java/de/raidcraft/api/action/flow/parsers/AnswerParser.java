package de.raidcraft.api.action.flow.parsers;

import de.raidcraft.api.action.flow.FlowException;
import de.raidcraft.api.action.flow.FlowExpression;
import de.raidcraft.api.action.flow.FlowParser;
import de.raidcraft.api.action.flow.types.FlowAnswer;

import java.util.regex.Pattern;

/**
 * @author mdoering
 */
public class AnswerParser extends FlowParser {

    public AnswerParser() {

        super(Pattern.compile("^:\"(.*)\"(->([\\w\\d\\-_\\.]+))?$"));
        // #0 	:"Hallo du, was geht ab?"->var1
        // #1	Hallo du, was geht ab?
        // #2	->var1
        // #3	var1
    }

    @Override
    protected FlowExpression parse() throws FlowException {

        String text = getMatcher().group(1);
        if (text == null) {
            throw new FlowException("Could not parse answer without text!");
        }
        String inputVar = getMatcher().group(3);
        return new FlowAnswer(text, inputVar);
    }
}

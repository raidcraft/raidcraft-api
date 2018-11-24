package de.raidcraft.api.action.flow.parsers;

import com.google.common.base.Strings;
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

        super(Pattern.compile("^(?<requirement>[+\\-]?):\"(?<text>.*)\"(->(?<var>[\\w\\d\\-_\\.]+))?$"));
        // #0 	:"Hallo du, was geht ab?"->var1
        // #1	Hallo du, was geht ab?
        // #2	->var1
        // #3	var1
    }

    @Override
    public FlowExpression parse() throws FlowException {

        String requirementGroup = getMatcher().group("requirement");
        boolean checkingRequirement = !Strings.isNullOrEmpty(requirementGroup);
        boolean negate = checkingRequirement && requirementGroup.equalsIgnoreCase("-");

        String text = getMatcher().group("text");
        if (text == null) {
            throw new FlowException("Could not parse answer without text!");
        }
        String inputVar = getMatcher().group("var");
        FlowAnswer flowAnswer = new FlowAnswer(text, inputVar);
        flowAnswer.setCheckingRequirement(checkingRequirement);
        flowAnswer.setNegate(negate);
        return flowAnswer;
    }
}

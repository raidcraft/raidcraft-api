package de.raidcraft.api.action.flow.parsers;

import de.raidcraft.api.action.flow.FlowException;
import de.raidcraft.api.action.flow.FlowParser;
import de.raidcraft.api.action.flow.FlowType;
import de.raidcraft.api.action.flow.types.FlowAlias;

import java.util.Optional;
import java.util.regex.Pattern;

public class VariableGroupParser extends FlowParser {

    public VariableGroupParser() {
        super(Pattern.compile("^([\\?!%])\\[([\\w\\d_-]+)\\](.*)$"));
        // #0	?[geld]player.money 10g2s
        // #1	?
        // #2	geld
        // #3	player.money 10g2s
    }

    @Override
    public FlowAlias parse() throws FlowException {

        String typeSymbol = getMatcher().group(1);
        Optional<FlowType> flowType = FlowType.fromString(typeSymbol);

        if (!flowType.isPresent()) {
            throw new FlowException("No flow type for the symbol " + typeSymbol + " found!");
        }

        FlowType type = flowType.get();
        String alias = getMatcher().group(2);
        if (getMatcher().group(3).isEmpty()) {
            return new FlowAlias(type, alias);
        }

        String expression = getMatcher().group(1) + getMatcher().group(3);

        // parse the cleaned flow expression
        switch (type) {
            case EXPRESSION:
                break;
            case ACTION:
            case REQUIREMENT:
                ActionApiFlowParser flowParser = new ActionApiFlowParser();
                if (!flowParser.accept(expression)) {
                    throw new FlowException("Cannot parse instructions into valid ActionApi Flow pattern: " + expression);
                }

                return new FlowAlias(type, alias, flowParser.parse());
            default:
                throw new FlowException("Cannot create an alias group for the given flow type: " + type.name());
        }

        throw new FlowException("Unable to parse alias group: " + getMatcher().group(0));
    }
}

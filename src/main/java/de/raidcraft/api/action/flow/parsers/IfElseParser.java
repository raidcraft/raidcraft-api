package de.raidcraft.api.action.flow.parsers;

import de.raidcraft.api.action.flow.FlowExpression;
import de.raidcraft.api.action.flow.FlowParser;
import de.raidcraft.api.action.flow.types.IfElse;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author mdoering
 */
public class IfElseParser extends FlowParser {

    private static final Pattern STATEMENT_PARSER = Pattern.compile("");

    public IfElseParser() {

        super(Pattern.compile("^(if|elseif|else|fi)[ ]?(.*)$"));
        // #0 	elseif withRequirement.test-1 val1, val2
        // #1	elseif
        // #2	withRequirement.test-1 val1, val2
    }

    @Override
    public FlowExpression parse() {

        // group1: keyworkd if, elseif, else or fi
        // group2: the withRequirement or empty
        String keyword = getMatcher().group(1);
        String statement = getMatcher().group(2);
        if (Objects.isNull(statement)) {
            return new IfElse(IfElse.Type.valueOf(keyword.toUpperCase()), Optional.empty());
        }
        // we need to parse the withRequirement recursivly and also respect the boundaries
        // parsing should be done from the inside out
        return null;
    }
}

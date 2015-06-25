package de.raidcraft.api.action.flow.parsers;

import de.raidcraft.api.action.flow.FlowException;
import de.raidcraft.api.action.flow.FlowExpression;
import de.raidcraft.api.action.flow.FlowParser;

import java.util.regex.Pattern;

/**
 * @author mdoering
 */
public class ActionParser extends FlowParser {

    public ActionParser() {

        super(Pattern.compile("^!([a-zA-Z_\\-\\.]+)\\(?(.*)\\)?(.*)$"));
    }

    @Override
    protected FlowExpression parse() throws FlowException {


    }
}

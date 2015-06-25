package de.raidcraft.api.action.flow.parsers;

import de.raidcraft.api.action.flow.FlowException;
import de.raidcraft.api.action.flow.FlowExpression;
import de.raidcraft.api.action.flow.FlowParser;

import java.util.regex.Pattern;

/**
 * @author mdoering
 */
public class ConfigParser extends FlowParser {

    public ConfigParser() {

        super(Pattern.compile("^((([\\s\\w\\d]+)=)?([\\w\\d\\s]+)[, ]?)*"));
    }

    @Override
    protected FlowExpression parse() throws FlowException {


    }
}

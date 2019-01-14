package de.raidcraft.api.action.flow.parsers;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.action.global.DynamicPlayerTextAction;
import de.raidcraft.api.action.flow.*;
import de.raidcraft.api.action.flow.types.ActionAPIType;

import java.util.regex.Pattern;

public class NpcTextParser extends FlowParser {

    private static final String TEXT_ACTION = "text";

    public NpcTextParser() {
        super(Pattern.compile("^(?<text>[a-zA-Z\"]+.*)$"));
    }

    @Override
    public FlowExpression parse() throws FlowException {

        FlowConfiguration configuration = new FlowConfiguration();
        ConfigParser configParser = ActionAPI.getActionInformation(TEXT_ACTION).map(ConfigParser::new).orElseGet(ConfigParser::new);
        if (configParser.accept("\"" + getMatcher().group("text") + "\"")) {
            configuration = configParser.parse();
        }
        return new ActionAPIType(FlowType.ACTION, configuration, TEXT_ACTION);
    }
}

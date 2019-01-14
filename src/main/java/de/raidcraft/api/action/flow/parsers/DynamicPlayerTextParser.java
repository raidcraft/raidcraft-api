package de.raidcraft.api.action.flow.parsers;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.action.global.DynamicPlayerTextAction;
import de.raidcraft.api.action.flow.*;
import de.raidcraft.api.action.flow.types.ActionAPIType;

import java.util.regex.Pattern;

public class DynamicPlayerTextParser extends FlowParser {

    public DynamicPlayerTextParser() {
        super(Pattern.compile("^>\"(?<text>.*)\""));
    }

    @Override
    public FlowExpression parse() throws FlowException {

        FlowConfiguration configuration = new FlowConfiguration();
        ConfigParser configParser = ActionAPI.getActionInformation(DynamicPlayerTextAction.ACTION_NAME).map(ConfigParser::new).orElseGet(ConfigParser::new);
        if (configParser.accept("\"" + getMatcher().group("text") + "\"")) {
            configuration = configParser.parse();
        }
        return new ActionAPIType(FlowType.DYNAMIC_ACTION, configuration, DynamicPlayerTextAction.ACTION_NAME);
    }
}

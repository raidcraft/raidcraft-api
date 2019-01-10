package de.raidcraft.api.action.flow.parsers;

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
        configuration.set("text", getMatcher().group("text"));
        return new ActionAPIType(FlowType.DYNAMIC_ACTION, configuration, "text.dynamic");
    }
}

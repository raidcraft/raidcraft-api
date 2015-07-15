package de.raidcraft.api.action.flow.parsers;

import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.flow.FlowConfiguration;
import de.raidcraft.api.action.flow.FlowException;
import de.raidcraft.api.action.flow.FlowParser;
import de.raidcraft.api.action.flow.FlowType;
import de.raidcraft.api.action.flow.types.ActionAPIType;
import de.raidcraft.api.action.flow.types.FlowDelay;
import de.raidcraft.api.config.builder.ConfigGenerator;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author mdoering
 */
public class ActionTypeParser extends FlowParser {

    public ActionTypeParser() {

        super(Pattern.compile("^([!@\\?~])([a-zA-Z\\-\\._\\d]+)([( ]?.*)$"));
        // #0 	@trigger(blubb: aaaa) world,1,2,3
        // #1	@
        // #2	trigger
        // #3	(blubb: aaaa) world,1,2,3
    }

    @Override
    protected ActionAPIType parse() throws FlowException {

        // first we need to find out the flow type from group 1
        String typeSymbol = getMatcher().group(1);
        Optional<FlowType> flowType = FlowType.fromString(typeSymbol);
        if (!flowType.isPresent()) {
            throw new FlowException("No flow type for the symbol " + typeSymbol + " found!");
        }
        FlowConfiguration configuration = new FlowConfiguration();
        String type = getMatcher().group(2);
        Optional<ConfigGenerator.Information> information = Optional.empty();
        switch (flowType.get()) {
            case DELAY:
                return new FlowDelay(type);
            case ACTION:
                information = ActionAPI.getActionInformation(type);
                break;
            case REQUIREMENT:
                information = ActionAPI.getRequirementInformation(type);
                break;
            case TRIGGER:
                information = ActionAPI.getTriggerInformation(type);
                break;
        }
        if (!information.isPresent()) {
            throw new FlowException("ConfigInformation is not present for " + type);
        }
        ConfigParser configParser = new ConfigParser(information.get());
        if (configParser.accept(getMatcher().group(3))) {
            // if the parser does not match the config is empty
            configuration = configParser.parse();
        }
        configuration.set("type", type);
        return new ActionAPIType(flowType.get(), configuration, type);
    }
}

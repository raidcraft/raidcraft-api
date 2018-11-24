package de.raidcraft.api.action.flow.parsers;

import com.google.common.base.Strings;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.flow.*;
import de.raidcraft.api.action.flow.types.ActionAPIType;
import de.raidcraft.api.action.flow.types.FlowDelay;
import de.raidcraft.api.config.builder.ConfigGenerator;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author mdoering
 */
public class ActionApiFlowParser extends FlowParser {

    private FlowConfigParser configParser;

    public ActionApiFlowParser() {

        super(Pattern.compile("^(?<requirement>[+\\-]?)(?<type>[!@\\?~])(?<name>[a-zA-Z\\-\\._\\d]+)(?<config>[( ]?.*)$"));
        // #0 	@trigger(blubb: aaaa) world,1,2,3
        // #type	@
        // #name	trigger
        // #config	(blubb: aaaa) world,1,2,3
        // #requirement +/-
    }

    public ActionApiFlowParser(FlowConfigParser configParser) {
        this();
        this.configParser = configParser;
    }

    private Optional<FlowConfigParser> getConfigParser() {
        return Optional.ofNullable(this.configParser);
    }

    @Override
    public ActionAPIType parse() throws FlowException {

        // first we need to find out the flow type from group 1
        String typeSymbol = getMatcher().group("type");
        Optional<FlowType> optionalFlowType = FlowType.fromString(typeSymbol);
        if (!optionalFlowType.isPresent()) {
            throw new FlowException("No flow type for the symbol " + typeSymbol + " found!");
        }
        FlowConfiguration configuration = new FlowConfiguration();
        String type = getMatcher().group("name");
        String requirementGroup = getMatcher().group("requirement");
        boolean checkingRequirement = !Strings.isNullOrEmpty(requirementGroup);
        boolean requirementFailure = checkingRequirement && requirementGroup.equalsIgnoreCase("-");
        FlowType flowType = optionalFlowType.get();
        Optional<ConfigGenerator.Information> information = Optional.empty();
        switch (flowType) {
            case DELAY:
                return new FlowDelay(type);
            case ACTION:
                if (!getConfigParser().filter(parser -> parser.hasAlias(FlowType.ACTION, type)).isPresent() && !ActionAPI.isAction(type)) {
                    ActionAPI.UNKNOWN_ACTIONS.add(type);
                    throw new FlowException(flowType.name() + " " + type + " inside flow not found!");
                }
                information = ActionAPI.getActionInformation(type);
                break;
            case REQUIREMENT:
                if (!getConfigParser().filter(parser -> parser.hasAlias(FlowType.REQUIREMENT, type)).isPresent() && !ActionAPI.isRequirement(type)) {
                    ActionAPI.UNKNOWN_REQUIREMENTS.add(type);
                    throw new FlowException(flowType.name() + " " + type + " inside flow not found!");
                }
                information = ActionAPI.getRequirementInformation(type);
                break;
            case TRIGGER:
                if (!ActionAPI.isTrigger(type)) {
                    ActionAPI.UNKNOWN_TRIGGER.add(type);
                    throw new FlowException(flowType.name() + " " + type + " inside flow not found!");
                }
                information = ActionAPI.getTriggerInformation(type);
                break;
        }
        ConfigParser configParser;

        if (getConfigParser().filter(parser -> parser.hasAlias(flowType, type)).isPresent()) {
            configParser = new ConfigParser();
        } else {
            configParser = information.map(ConfigParser::new).orElseGet(ConfigParser::new);
        }

        if (configParser.accept(getMatcher().group("config"))) {
            // if the parser does not match the config is empty
            configuration = configParser.parse();
        }

        configuration.set("type", type);
        ActionAPIType actionAPIType = new ActionAPIType(flowType, configuration, type);
        actionAPIType.setCheckingRequirement(checkingRequirement);
        actionAPIType.setNegate(requirementFailure);
        return actionAPIType;
    }
}

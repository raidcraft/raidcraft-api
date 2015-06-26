package de.raidcraft.api.action.flow.parsers;

import de.raidcraft.api.action.flow.FlowConfiguration;
import de.raidcraft.api.action.flow.FlowException;
import de.raidcraft.api.action.flow.FlowParser;
import de.raidcraft.api.config.builder.ConfigGenerator;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mdoering
 */
public class ConfigParser extends FlowParser {

    private static final Pattern GLOBAL_PARAMS_PATTERN = Pattern.compile("^\\(((([a-zA-Z\\-_\\.<>]+):)?(.*))\\)(.*)$");
    private static final Pattern VALUE_PATTERN = Pattern.compile("^([\\w\\d\\-_\\.'<>]+)[ ,]?(.*)$");

    private final ConfigGenerator.Information configInformation;

    public ConfigParser(ConfigGenerator.Information configInformation) {

        super(Pattern.compile("^[^@\\^:?~]((([a-zA-Z\\d\\-_\\.<>]+):)?(.+))$"));
        this.configInformation = configInformation;
    }

    @Override
    protected FlowConfiguration parse() throws FlowException {

        FlowConfiguration config = new FlowConfiguration();
        String input = getInput();
        // lets first check if the input has global params and extract them
        // #0 	(key:value key2:value) key3:"value in brackets" key4:
        // #1	key:value key2:value
        // #2	key:
        // #3	key
        // #4	value key2:value
        // #5	 key3:"value in brackets" key4:
        Matcher matcher = GLOBAL_PARAMS_PATTERN.matcher(input);
        if (matcher.matches()) {
            // ok we found global params, lets extract them
            config = extractKeyValuePairs(config, matcher.group(1), -1);
            // adjust and trim the remaining input
            String remainingInput = matcher.group(5);
            if (remainingInput != null) remainingInput = remainingInput.trim();
            input = remainingInput;
        }
        if (input != null) {
            FlowConfiguration argsConfig = extractKeyValuePairs(new FlowConfiguration(), input, 0);
            config.set("args", argsConfig);
        }
        return config;
    }

    private FlowConfiguration extractKeyValuePairs(FlowConfiguration config, String input, int position) throws FlowException {

        // format:info text:"Öffne dein Quest Inventar mit /qi"
        // #0 	format:info text:"Öffne dein Quest Inventar mit /qi"
        // #1	format:info text:"Öffne dein Quest Inventar mit /qi"
        // #2	format:
        // #3	format
        // #4	info text:"Öffne dein Quest Inventar mit /qi"

        // lets first get our value from the 4th group
        // if there are no quotations we will split at a semikolon or space
        Matcher matcher = getPattern().matcher(input);
        if (!matcher.matches()) {
            // we are done, there is nothing left to match
            return config;
        }
        // can be null if parameters are defined based on their position, e.g. world,1,2,3
        String key = matcher.group(3);
        // if the key is null we need to extract the key from the config information
        if (key == null) {
            if (position < 0) {
                // if the position is -1 positional params are not allowed
                throw new FlowException("Positional params are not allowed, please specify a key!");
            }
            if (position < configInformation.conf().length) {
                key = configInformation.conf()[position];
            } else {
                throw new FlowException("Could not find config key in " + configInformation.value() + " on position " + position);
            }
        }

        // okay now that we have our key it is time to extract the value
        // but we only to extract the first matched value and then recursibly pass the rest back to this method
        String matchedValue = matcher.group(4);
        if (matchedValue == null) {
            // seems like we have a key without a value
            throw new FlowException("Key " + key + " without value defined!");
        }

        String value;
        String remaining = null;
        if (matchedValue.startsWith("\"")) {
            int endIndex = matchedValue.indexOf("\"", 1);
            value = matchedValue.substring(1, endIndex);
            if (endIndex < matchedValue.length()) remaining = matchedValue.substring(endIndex);
        } else {
            // lets apply our value matcher to extract the matched value
            // world,1,2,3 or info text:"Öffne dein Quest Inventar mit /qi"
            // #0 	world,1,2,3 or info text:"Öffne dein Quest Inventar mit /qi"
            // #1	world or info
            // #2	1,2,3 or text:"Öffne dein Quest Inventar mit /qi"
            Matcher valueMatcher = VALUE_PATTERN.matcher(matchedValue);
            if (!valueMatcher.matches()) {
                // for some reason the value string does not match
                // if this is the case we either need to improve our code or the config is wrong
                throw new FlowException("Could not find matching value for key " + key);
            }
            // the 1st group is our value
            value = valueMatcher.group(1);
            // and the 2nd group is the remainder
            remaining = valueMatcher.group(2);
        }
        // ok lets set the key value pair if everything is ok
        if (Objects.isNull(key) || Objects.isNull(value)) {
            throw new FlowException("Key or Value is null: " + key + ":" + value);
        }
        config.set(key, value);
        // and now we recurse with the remainung match if there is any
        if (remaining != null) remaining = remaining.trim();
        if (Objects.isNull(remaining)) {
            // ok we are done, lets return
            return config;
        }
        return extractKeyValuePairs(config, remaining, position + 1);
    }
}

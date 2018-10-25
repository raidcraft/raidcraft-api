package de.raidcraft.api.action.flow.parsers;

import de.raidcraft.api.action.flow.FlowConfiguration;
import de.raidcraft.api.action.flow.FlowException;
import de.raidcraft.api.action.flow.FlowParser;
import de.raidcraft.api.config.builder.ConfigGenerator;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author mdoering
 */
public class ConfigParser extends FlowParser {

    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("^(([\\w\\d_\\-\\.]+):)?(((\"(.*)\")|([öäüß\\w\\d_\\-\\.]+)|(\\{(.*)\\})|(\\[(.*)\\]))[ ,]?(.*))$");

    private ConfigGenerator.Information configInformation;

    public ConfigParser() {
        super(Pattern.compile("^(\\(?(.*)\\))?(.*)$"));
    }

    public ConfigParser(ConfigGenerator.Information configInformation) {

        // we accept everything but parse for a global config section
        // #0 	(blubb: aaaa) world:azuran,1,2,3
        // #1	(blubb: aaaa)
        // #2	blubb: aaaa
        // #3	 world:azuran,1,2,3
        this();
        this.configInformation = configInformation;
    }

    public Optional<ConfigGenerator.Information> getConfigInformation() {
        return Optional.ofNullable(this.configInformation);
    }

    @Override
    public FlowConfiguration parse() throws FlowException {

        FlowConfiguration config = new FlowConfiguration();
        // lets first check if the input has global params and extract them
        // #0 	(key:value key2:value) key3:"value in brackets" key4:
        // #1	(key:value key2:value)
        // #2	key:value key2:value
        // #3	 key3:"value in brackets" key4:
        Matcher matcher = getMatcher();
        String globalParams = matcher.group(2);
        String params = matcher.group(3);
        if (globalParams != null) {
            // ok we found global params, lets extract them
            // -1 of a startStage position means that no positional params are allowed
            config = extractKeyValuePairs(config, globalParams, -1);
        }
        if (params != null) {
            params = params.trim();
            FlowConfiguration argsConfig = extractKeyValuePairs(new FlowConfiguration(), params, 0);
            config.set("args", argsConfig);
        }
        return config;
    }

    private FlowConfiguration extractKeyValuePairs(FlowConfiguration config, String input, int position) throws FlowException {

        // #0 	1, key3:"value in brackets" key4:{value1, value2}, key5:foo, key7:[val1, val2]
        // #1	null
        // #2	null
        // #3	1, key3:"value in brackets" key4:{value1, value2}, key5:foo, key7:[val1, val2]
        // #4	1
        // #5	null
        // #6	null
        // #7	1
        // #8	null
        // #9	null
        // #10	null
        // #11	null
        // #12	 key3:"value in brackets" key4:{value1, value2}, key5:foo, key7:[val1, val2]

        // first we will check if we need to process any key/value pairs
        Matcher matcher = KEY_VALUE_PATTERN.matcher(input);
        if (!matcher.matches()) {
            // we are done, there is nothing left to match
            return config;
        }

        // can be null if parameters are defined based on their position, e.g. world,1,2,3
        // group 2: will usually be the key if any exists
        String key = matcher.group(2);
        // if the key is null we need to extract the key from the config information
        if (key == null) {
            if (!getConfigInformation().isPresent()) {
                throw new FlowException("Could not extract config key " + configInformation.value() + " from position " + (position - 1) + " because not @Information tag is specified!");
            }
            Optional<String> optionalKey = extractKeyFromInformation(configInformation, position++);
            if (!optionalKey.isPresent()) {
                throw new FlowException("Could not extract config key " + configInformation.value() + " from position " + (position - 1));
            }
            key = optionalKey.get();
        }

        String value = null;
        // okay now that we have our key it is time to extract the value
        // but we only extract the first matched value and then recursivly pass the rest back to this method
        // we also need to differ between different type of values, like arrays, config sections and numbers
        // group 7: will be the value if no special value is defined
        // group 6: will be a special value in quotation marks, e.g.: key3:"value in brackets"
        // group 9: will be a config section inside squirly brackets, e.g: key4:{key:value1, key2:value2}
        // group 11: will be an array definition inside square brackets, e.g. key7:[val1, val2]
        if (!Objects.isNull(matcher.group(7))) {
            value = matcher.group(7);
        } else if (!Objects.isNull(matcher.group(6))) {
            value = matcher.group(6);
        } else if (!Objects.isNull(matcher.group(9))) {
            // ok we have a config section and need to parse the key value pairs
            FlowConfiguration subSection = extractKeyValuePairs(new FlowConfiguration(), matcher.group(9), -1);
            config.set(key, subSection);
        } else if (!Objects.isNull(matcher.group(11))) {
            // seems like we have an array definition
            String[] array = matcher.group(11).split(",");
            for (int i = 0; i < array.length; i++) {
                array[i] = array[i].trim();
            }
            // lets convert the arry to a int, double or string list
            if (array.length > 0) {
                try {
                    config.set(key, Arrays.stream(array).map(Integer::parseInt).collect(Collectors.toList()));
                } catch (NumberFormatException ex) {
                    try {
                        config.set(key, Arrays.stream(array).map(Double::parseDouble).collect(Collectors.toList()));
                    } catch (NumberFormatException ignored) {
                        config.set(key, Arrays.asList(array));
                    }
                }
            }
        }
        // ok lets see if we have a value and parse its type
        if (!Objects.isNull(value)) {
            value = value.trim();
            boolean parsed = false;
            try {
                int val = Integer.parseInt(value);
                config.set(key, val);
                parsed = true;
            } catch (NumberFormatException ignored) {
            }
            if (!parsed) {
                try {
                    double val = Double.parseDouble(value);
                    config.set(key, val);
                    parsed = true;
                } catch (NumberFormatException ignored) {
                }
            }
            if (!parsed && value.matches("true|false")) {
                config.set(key, Boolean.parseBoolean(value));
                parsed = true;
            }
            if (!parsed) {
                config.set(key, value);
            }
        }
        // group 12: will be the remaining key/values
        String remainder = matcher.group(12);
        if (!Objects.isNull(remainder)) {
            return extractKeyValuePairs(config, remainder.trim(), position);
        }
        return config;
    }

    private Optional<String> extractKeyFromInformation(ConfigGenerator.Information information, int position) throws FlowException {

        if (position < 0) {
            // if the position is -1 positional params are not allowed
            throw new FlowException("Positional params are not allowed, please specify a key!");
        }
        if (position < information.conf().length) {
            // extract the key by position and move to the next position
            String key = information.conf()[position];
            String[] strings = key.split(":");
            return Optional.ofNullable(strings[0]);
        }
        throw new FlowException("Could not find config key in " + configInformation.value() + " on position " + position);
    }
}

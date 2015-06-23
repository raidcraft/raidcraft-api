package de.raidcraft.api.action.flow;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.TriggerFactory;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.trigger.Trigger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mdoering
 */
public class FlowParser {

    private static final Pattern ACTION_PARAMETERS = Pattern.compile("^([!?@:])([a-zA-Z\\.]+)(\\((.*)\\))?.*$");

    private final String[] lines;
    private double totalDelay = 0;

    public FlowParser(String... lines) {

        this.lines = lines;
    }

    public ConfigurationSection parseActions() throws ParseException {

        // reset our delay
        totalDelay = 0;
        // if the first flow type is not an action abort
        if (lines.length < 1 || !lines[0].startsWith("!")) {
            throw new ParseException("Expected an action (!) as first parameter, but found " + lines[0].charAt(0), 0);
        }

        ConfigurationSection currentAction;

        ConfigurationSection root = new MemoryConfiguration();
        for (int i = 0; i < lines.length; i++) {
            Matcher matcher = ACTION_PARAMETERS.matcher(lines[i]);
            // @host.proximity(cooldown:3s) this.karl
            // Capture groups
            // #0 	@host.proximity(cooldown:3s) this.karl
            // #1	@
            // #2	host.proximity
            // #3	(cooldown:3s) this.karl
            // #4	(cooldown:3s)
            // #5	cooldown:3s
            if (matcher.matches()) {
                String group1 = matcher.group(1);
                Optional<FlowType> flowType = FlowType.fromString(group1);
                if (!flowType.isPresent()) {
                    throw new ParseException("Failed to parse flow actions! Wrong flow type: " + group1, 0);
                }
                ConfigurationSection section = new MemoryConfiguration();
                switch (flowType.get()) {
                    case ACTION:
                        currentAction = root.createSection("'" + i + "'");
                        section = currentAction;
                    case REQUIREMENT:
                    case TRIGGER:
                        section.set("type", matcher.group(2));
                }
                parseParameters(matcher.group(3), section);
            }
        }
    }

    private ConfigurationSection parseParameters(String line, ConfigurationSection section) {


    }
}

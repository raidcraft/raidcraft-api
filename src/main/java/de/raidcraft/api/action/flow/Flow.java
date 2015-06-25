package de.raidcraft.api.action.flow;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * @author mdoering
 */
public final class Flow {

    private static final List<FlowParser> parsers = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public static <T> List<Requirement<T>> parseRequirements(ConfigurationSection config, Class<T> type) {

        List<Requirement<T>> requirements = new ArrayList<>();

        config.getKeys(false).stream()
                // if the node is a list we're gonna flow away :)
                .filter(config::isList)
                .forEach(key -> {
                    try {
                        List<FlowExpression> expressions = parse(config.getStringList(key), type);

                        for (int i = 0; i < expressions.size(); i++) {
                            FlowExpression flowExpression = expressions.get(i);
                            if (i == 0 && (!(flowExpression instanceof Requirement) || ActionAPI.matchesType((Requirement<?>) flowExpression, type))) {
                                throw new FlowException("Error when parsing flow requirements! " +
                                        "The first flow statement must be a requirement inside a requirement block!");
                            }
                            if (flowExpression instanceof Requirement) {
                                if (ActionAPI.matchesType((Requirement<?>) flowExpression, type)) {
                                    requirements.add((Requirement<T>) flowExpression);
                                }
                            }
                        }
                    } catch (FlowException e) {
                        RaidCraft.LOGGER.warning(e.getMessage() + " in " + ConfigUtil.getFileName(config));
                        e.printStackTrace();
                    }
                });

        return requirements;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<Action<T>> parseActions(ConfigurationSection config, Class<T> type) {

        List<Action<T>> actions = new ArrayList<>();

        config.getKeys(false).stream()
                // if the node is a list we're gonna flow away :)
                .filter(config::isList)
                .forEach(key -> {
                    try {
                        List<FlowExpression> expressions = parse(config.getStringList(key), type);

                        for (int i = 0; i < expressions.size(); i++) {
                            FlowExpression flowExpression = expressions.get(i);
                            if (i == 0 && (!(flowExpression instanceof Action) || ActionAPI.matchesType((Action<?>) flowExpression, type))) {
                                throw new FlowException("Error when parsing flow actions! " +
                                        "The first flow statement must be an action inside an action block!");
                            }
                            if (flowExpression instanceof Action) {
                                if (ActionAPI.matchesType((Action<?>) flowExpression, type)) {
                                    actions.add((Action<T>) flowExpression);
                                }
                            }
                        }
                    } catch (FlowException e) {
                        RaidCraft.LOGGER.warning(e.getMessage() + " in " + ConfigUtil.getFileName(config));
                        e.printStackTrace();
                    }
                });

        return actions;
    }

    public static <T> List<FlowExpression> parse(List<String> lines, Class<T> type) throws FlowException {

        List<FlowExpression> expressions = new ArrayList<>();

        Iterator<String> iterator = lines.iterator();
        while (iterator.hasNext()) {
            for (FlowParser parser : parsers) {
                while (iterator.hasNext()) {
                    if (parser.accept(iterator.next())) {
                        expressions.add(parser.parse(type));
                    } else {
                        parser.close();
                        break;
                    }
                }
            }
        }

        return expressions;
    }

    public static <T> Optional<FlowExpression> parse(String line, Class<T> type) throws FlowException {

        for (FlowParser parser : parsers) {
            if (parser.accept(line)) {
                return Optional.of(parser.parse(type));
            }
        }
        return Optional.empty();
    }
}

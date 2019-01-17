package de.raidcraft.api.action.requirement.global;

import com.google.common.base.Strings;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.tags.TPlayerTag;
import de.raidcraft.util.TimeUtil;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagRequirement implements Requirement<Player> {

    @Information(
            value = "player.tag",
            aliases = {"tag"},
            desc = "Checks if the player has the given tag.",
            conf = {
                    "tag: id of the tag",
                    "count: checks the count status of the tag, e.g.: >5, <5, >=5, <=5, =5",
                    "ignore-duration: ignores the duration and only checks the existance (default: false)"
            }
    )
    @Override
    public boolean test(Player player, ConfigurationSection config) {

        return TPlayerTag.findTag(player.getUniqueId(), config.getString("tag")).map(tag -> {
            boolean matchesCount = Count.parseCount(config.getString("count"))
                    .map(count -> count.matches(tag.getCount()))
                    .orElse(true);

            if (!Strings.isNullOrEmpty(tag.getDuration()) && !config.getBoolean("ignore-duration", false)) {
                // check if the tag has expired
                return matchesCount && tag.getWhenModified().plusMillis(TimeUtil.parseTimeAsMillis(tag.getDuration())).isAfter(Instant.now());
            }
            return matchesCount;
        }).orElse(false);
    }

    @Data
    public static class Count {

        private static final Pattern COUNT_PATTERN = Pattern.compile("^[=><]+(\\d)+$");

        public static Optional<Count> parseCount(String count) {
            if (Strings.isNullOrEmpty(count)) return Optional.empty();
            return Operator.fromString(count).map(operator -> new Count(operator, parseCountValue(count)));
        }

        private static int parseCountValue(String count) {
            Matcher matcher = COUNT_PATTERN.matcher(count);
            if (matcher.matches()) {
                return Integer.parseInt(matcher.group(2));
            }
            return 0;
        }

        private final Operator operator;
        private final int count;

        public boolean matches(int value) {
            switch (getOperator()) {
                case EQUALS:
                    return value == getCount();
                case GREATER_EQUALS:
                    return value >= getCount();
                case GREATER_THEN:
                    return value > getCount();
                case LESS_EQUALS:
                    return value <= getCount();
                case LESS_THEN:
                    return value < getCount();
                default:
                    return false;
            }
        }
    }

    public enum Operator {

        GREATER_THEN(">"),
        LESS_THEN(">"),
        GREATER_EQUALS(">=", "=>"),
        LESS_EQUALS("<=", "=<"),
        EQUALS("=", "==");

        private final String[] operators;

        Operator(String... operators) {
            this.operators = operators;
        }

        public static Optional<Operator> fromString(String string) {

            for (Operator value : Operator.values()) {
                for (String operator : value.operators) {
                    if (string.startsWith(operator)) return Optional.of(value);
                }
            }
            return Optional.empty();
        }
    }
}

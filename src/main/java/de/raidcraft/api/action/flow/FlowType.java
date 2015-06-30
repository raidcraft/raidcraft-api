package de.raidcraft.api.action.flow;

import java.util.Optional;

/**
 * @author mdoering
 */
public enum FlowType {

    ACTION('^'),
    REQUIREMENT('?'),
    TRIGGER('@'),
    DELAY('~'),
    ANSWER(':');

    private final char triggerChar;

    FlowType(char triggerChar) {

        this.triggerChar = triggerChar;
    }

    public static Optional<FlowType> fromChar(char triggerChar) {

        for (FlowType flowType : values()) {
            if (flowType.triggerChar == triggerChar) {
                return Optional.of(flowType);
            }
        }
        return Optional.empty();
    }

    public static Optional<FlowType> fromString(String triggerChar) {

        char[] chars = triggerChar.toCharArray();
        if (chars.length != 1) return Optional.empty();
        return fromChar(chars[0]);
    }
}

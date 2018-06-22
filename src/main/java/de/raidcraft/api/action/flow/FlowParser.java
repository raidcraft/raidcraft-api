package de.raidcraft.api.action.flow;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mdoering
 */
@Data
@RequiredArgsConstructor
public abstract class FlowParser {

    private final Pattern pattern;
    private Matcher matcher;
    private String input;
    private Class<?> type;

    public boolean accept(String line) {

        matcher = pattern.matcher(line);
        input = line;
        return matcher.matches();
    }

    public abstract FlowExpression parse() throws FlowException;
}

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
    private String currentLine;
    private Class<?> type;

    public boolean accept(String line) {

        matcher = pattern.matcher(line);
        if (matcher.matches()) {
            currentLine = line;
            return true;
        } else if (currentLine != null) {

        }
        matcher = null;
        currentLine = null;
        return false;
    }

    public <T> FlowExpression parse(Class<T> type) throws FlowException {

        if (getCurrentLine() == null) {
            throw new FlowException("Parser did not match, make sure to call accept(String) " +
                    "first and only call parse if true was returned!");
        }
        this.type = type;
        return parse();
    }

    protected abstract FlowExpression parse() throws FlowException;

    public void close() throws FlowException {

    }
}

package de.raidcraft.api.action.flow.types;

import de.raidcraft.api.action.flow.FlowExpression;
import de.raidcraft.api.action.requirement.Requirement;
import lombok.Data;

import java.util.Optional;

/**
 * @author mdoering
 */
@Data
public class IfElse implements FlowExpression {

    public enum Type {
        IF,
        ELSE,
        IFELSE,
        FI
    }

    private final Type type;
    private final Optional<Requirement<?>> statement;
}

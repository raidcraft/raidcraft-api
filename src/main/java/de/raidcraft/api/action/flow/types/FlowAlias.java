package de.raidcraft.api.action.flow.types;

import de.raidcraft.api.action.flow.FlowExpression;
import de.raidcraft.api.action.flow.FlowType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * A single alias for a flow expression.
 */
@Data
public class FlowAlias implements FlowExpression {

    private final FlowType flowType;
    private final String alias;
    private final List<FlowExpression> expressions = new ArrayList<>();

    public FlowAlias(FlowType flowType, String alias) {
        this.flowType = flowType;
        this.alias = alias;
    }

    public FlowAlias(FlowType flowType, String alias, FlowExpression expression) {
        this.flowType = flowType;
        this.alias = alias;
        this.expressions.add(expression);
    }

    public FlowAlias(String alias, ActionAPIType expression) {
        this.flowType = expression.getFlowType();
        this.alias = alias;
        this.expressions.add(expression);
    }

    public FlowAlias(FlowType type, String alias, List<ActionAPIType> expressions) {
        this.flowType = type;
        this.alias = alias;
        this.expressions.addAll(expressions);
    }
}

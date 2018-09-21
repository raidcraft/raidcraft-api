package de.raidcraft.api.action.flow.types;

import de.raidcraft.api.action.flow.FlowConfiguration;
import de.raidcraft.api.action.flow.FlowExpression;
import de.raidcraft.api.action.flow.FlowType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author mdoering
 */
@Data
@RequiredArgsConstructor
public class ActionAPIType implements FlowExpression {

    private final FlowType flowType;
    private final FlowConfiguration configuration;
    private final String typeId;
    private Class<?> type;
}

package de.raidcraft.api.action.flow.types;

import de.raidcraft.api.action.flow.FlowConfiguration;
import de.raidcraft.api.action.flow.FlowType;
import de.raidcraft.util.TimeUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowDelay extends ActionAPIType {

    /**
     * Delay in ticks
     */
    private final long delay;

    public FlowDelay(String input) {

        this(TimeUtil.parseTimeAsTicks(input));
    }

    public FlowDelay(long delay) {

        super(FlowType.DELAY, new FlowConfiguration(), null);
        this.delay = delay;
    }
}

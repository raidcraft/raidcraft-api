package de.raidcraft.api.items.tooltip;

import de.raidcraft.util.CustomItemUtil;

/**
 * @author Silthus
 */
public class FixedMultilineTooltip extends Tooltip {

    public FixedMultilineTooltip(TooltipSlot slot, String... lines) {

        super(slot);
        setTooltip(lines);
    }

    @Override
    protected void updateLineWidth(String... tooltip) {

        for (String line : tooltip) {
            if (getWidth() < CustomItemUtil.getStringWidth(line)) {
                setWidth(CustomItemUtil.getStringWidth(line));
            }
        }
    }
}

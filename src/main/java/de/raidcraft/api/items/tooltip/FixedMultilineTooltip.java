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
    protected void updateLineWidth() {

        for (String line : getTooltip()) {
            if (getWidth() < CustomItemUtil.getStringWidth(line)) {
                setWidth(CustomItemUtil.getStringWidth(line));
            }
        }
    }
}

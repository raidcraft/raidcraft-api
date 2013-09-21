package de.raidcraft.api.items.tooltip;

import de.raidcraft.util.CustomItemUtil;

/**
 * @author Silthus
 */
public class FixedMultilineTooltip extends Tooltip {

    private final String[] lines;

    public FixedMultilineTooltip(TooltipSlot slot, String... lines) {

        super(slot);
        this.lines = lines;
    }

    @Override
    protected void updateLineWidth() {

        for (String line : lines) {
            if (getWidth() < CustomItemUtil.getStringWidth(line)) {
                setWidth(CustomItemUtil.getStringWidth(line));
            }
        }
    }

    @Override
    public String[] getTooltip() {

        return lines;
    }
}

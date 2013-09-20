package de.raidcraft.api.items;

import de.raidcraft.util.CustomItemUtil;

/**
 * @author Silthus
 */
public abstract class Tooltip {

    public static final String LINE_SEPARATOR = "->";
    public static final int DEFAULT_WIDTH = 150;

    private final TooltipSlot slot;
    private int width;

    public Tooltip(TooltipSlot slot) {

        this.slot = slot;
    }

    public TooltipSlot getSlot() {

        return slot;
    }

    public void setWidth(int width) {

        this.width = width;
        updateLineWidth();
    }

    public int getWidth() {

        if (this.width > DEFAULT_WIDTH) {
            return this.width;
        }
        int maxWidth = DEFAULT_WIDTH;
        for (String line : getTooltip()) {

            int width = CustomItemUtil.checkWidth(line, maxWidth, true);
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        setWidth(maxWidth);
        return maxWidth;
    }

    protected abstract void updateLineWidth();

    public abstract String[] getTooltip();
}

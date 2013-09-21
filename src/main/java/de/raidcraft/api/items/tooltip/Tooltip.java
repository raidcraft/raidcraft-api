package de.raidcraft.api.items.tooltip;

/**
 * @author Silthus
 */
public abstract class Tooltip {

    public static final String LINE_SEPARATOR = "->";
    public static final int DEFAULT_WIDTH = 150;

    private final TooltipSlot slot;
    private int width = DEFAULT_WIDTH;

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

        return width;
    }

    protected abstract void updateLineWidth();

    public abstract String[] getTooltip();
}

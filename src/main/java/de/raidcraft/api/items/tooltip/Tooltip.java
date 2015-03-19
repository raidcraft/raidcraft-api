package de.raidcraft.api.items.tooltip;

import de.raidcraft.util.CustomItemUtil;

/**
 * @author Silthus
 */
public abstract class Tooltip {

    public static final String LINE_SEPARATOR = "->";
    public static final int DEFAULT_WIDTH = 150;

    private final TooltipSlot slot;
    private String[] tooltip;
    private int width = DEFAULT_WIDTH;

    public Tooltip(TooltipSlot slot) {

        this.slot = slot;
    }

    public TooltipSlot getSlot() {

        return slot;
    }

    public void setWidth(int width) {

        if (width != this.width) {
            this.width = width;
            updateLineWidth();
        }
    }

    public int getWidth() {

        return width;
    }

    protected abstract void updateLineWidth();

    protected final void setTooltip(String... tooltip) {

        for (int i = 0; i < tooltip.length; i++) {
            tooltip[i] = CustomItemUtil.encodeItemId(getSlot().ordinal()) + tooltip[i];
        }
        this.tooltip = tooltip;
    }

    public final String[] getTooltip() {

        return tooltip;
    }
}

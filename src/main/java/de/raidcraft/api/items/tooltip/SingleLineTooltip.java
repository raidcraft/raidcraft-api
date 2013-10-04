package de.raidcraft.api.items.tooltip;

import de.raidcraft.util.CustomItemUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class SingleLineTooltip extends Tooltip {

    protected String[] tooltip;
    protected ChatColor color;

    public SingleLineTooltip(TooltipSlot slot, String text, ChatColor color) {

        super(slot);
        this.color = color == null ? ChatColor.WHITE : color;
        this.tooltip = new String[]{this.color + text};
        updateLineWidth();
    }

    public SingleLineTooltip(TooltipSlot slot, String text) {

        this(slot, text, null);
    }

    @Override
    protected void updateLineWidth() {

        for (int i = 0; i < this.tooltip.length; i++) {
            if (this.tooltip[i].contains(LINE_SEPARATOR)) {
                String[] split = this.tooltip[i].split(LINE_SEPARATOR);
                String buffer = StringUtils.repeat(" ", (getWidth() - CustomItemUtil.getStringWidth(split[0] + split[1])) / 4);
                this.tooltip[i] = color + split[0] + buffer + split[1];
            }
        }
    }

    @Override
    public String[] getTooltip() {

        return tooltip;
    }
}

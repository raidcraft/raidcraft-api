package de.raidcraft.api.items.tooltip;

import de.raidcraft.util.CustomItemUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class SingleLineTooltip extends Tooltip {

    protected ChatColor color;

    public SingleLineTooltip(TooltipSlot slot, String text, ChatColor color) {

        super(slot);
        this.color = color == null ? ChatColor.WHITE : color;
        setTooltip(this.color + text);
        updateLineWidth();
    }

    public SingleLineTooltip(TooltipSlot slot, String text) {

        this(slot, text, null);
    }

    @Override
    protected void updateLineWidth() {

        String[] tooltip = getTooltip();
        for (int i = 0; i < tooltip.length; i++) {
            if (tooltip[i].contains(LINE_SEPARATOR)) {
                String[] split = tooltip[i].split(LINE_SEPARATOR);
                String buffer = StringUtils.repeat(" ", (getWidth() - CustomItemUtil.getStringWidth(split[0] + split[1])) / 4);
                tooltip[i] = color + split[0] + buffer + split[1];
            }
        }
        setTooltip(tooltip);
    }
}

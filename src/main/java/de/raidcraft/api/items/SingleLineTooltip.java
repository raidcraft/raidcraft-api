package de.raidcraft.api.items;

import de.raidcraft.util.CustomItemUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class SingleLineTooltip extends Tooltip {

    private final String[] text;
    private final ChatColor color;

    public SingleLineTooltip(TooltipSlot slot, String text, ChatColor color) {

        super(slot);
        this.text = new String[]{color + text};
        this.color = color;
        updateLineWidth();
    }

    public SingleLineTooltip(TooltipSlot slot, String text) {

        this(slot, text, null);
    }

    @Override
    protected void updateLineWidth() {

        for (int i = 0; i < this.text.length; i++) {
            if (this.text[i].contains(LINE_SEPARATOR)) {
                String[] split = this.text[i].split(LINE_SEPARATOR);
                String buffer = StringUtils.repeat(" ", (getWidth() - CustomItemUtil.getStringWidth(split[0] + split[1])) / 4);
                this.text[i] = color + split[0] + buffer + split[1];
            }
        }
    }

    @Override
    public String[] getTooltip() {

        return text;
    }
}

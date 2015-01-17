package de.raidcraft.api.items.tooltip;

import de.raidcraft.util.CustomItemUtil;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class NameTooltip extends Tooltip {

    private final String[] tooltip;

    public NameTooltip(int id, String name, ChatColor color) {

        super(TooltipSlot.NAME);
        tooltip = new String[]{CustomItemUtil.encodeItemId(id) + color + name};
    }

    @Override
    protected void updateLineWidth() {


    }

    @Override
    public String[] getTooltip() {

        return tooltip;
    }
}

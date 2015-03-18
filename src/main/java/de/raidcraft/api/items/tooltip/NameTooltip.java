package de.raidcraft.api.items.tooltip;

import de.raidcraft.util.CustomItemUtil;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class NameTooltip extends Tooltip {

    public NameTooltip(int id, String name, ChatColor color) {

        super(TooltipSlot.NAME);
        setTooltip(CustomItemUtil.encodeItemId(id) + color + name);
    }

    @Override
    protected void updateLineWidth() {


    }
}

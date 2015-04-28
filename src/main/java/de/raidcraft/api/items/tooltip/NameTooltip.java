package de.raidcraft.api.items.tooltip;

import de.raidcraft.util.CustomItemUtil;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class NameTooltip extends Tooltip {

    private final String encodedName;

    public NameTooltip(int id, String name, ChatColor color) {

        super(TooltipSlot.NAME);
        encodedName = CustomItemUtil.encodeItemId(id) + color + name;
    }

    @Override
    protected void updateLineWidth(String... tooltip) {


    }

    @Override
    public String[] getTooltip() {

        return new String[]{encodedName};
    }
}

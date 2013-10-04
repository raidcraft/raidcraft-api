package de.raidcraft.api.items.tooltip;

import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class EquipmentTypeTooltip extends SingleLineTooltip {

    private final String text;
    private ChatColor color;
    private String[] tooltip;

    public EquipmentTypeTooltip(String text, ChatColor color) {

        super(TooltipSlot.EQUIPMENT_TYPE, text, color);
        this.color = color == null ? ChatColor.WHITE : color;
        this.text = text;
        this.tooltip = new String[]{this.color + text};
        updateLineWidth();
    }

    public EquipmentTypeTooltip(String tooltip) {

        this(tooltip, null);
    }

    public void setColor(ChatColor color) {

        this.color = color;
        this.tooltip = new String[]{color + text};
    }

    @Override
    public String[] getTooltip() {

        return tooltip;
    }
}

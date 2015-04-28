package de.raidcraft.api.items.tooltip;

import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class EquipmentTypeTooltip extends SingleLineTooltip {

    private String text;

    public EquipmentTypeTooltip(String text, ChatColor color) {

        super(TooltipSlot.EQUIPMENT_TYPE, text, color);
        this.color = color == null ? ChatColor.WHITE : color;
        this.text = text;
        String tooltip = this.color + text;
        setTooltip(tooltip);
        updateLineWidth(tooltip);
    }

    public EquipmentTypeTooltip(String tooltip) {

        this(tooltip, null);
    }

    public void setColor(ChatColor color) {

        this.color = color;
        setTooltip(color + text);
        updateLineWidth(color + text);
    }
}

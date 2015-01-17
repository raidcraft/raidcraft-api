package de.raidcraft.api.items.tooltip;

import de.raidcraft.util.CustomItemUtil;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class DPSTooltip extends Tooltip {

    private final double dps;
    private double equipedDps;
    private String lineMessage;

    public DPSTooltip(double dps) {

        super(TooltipSlot.DPS);
        this.dps = dps;
        this.equipedDps = dps;
        lineMessage = ChatColor.WHITE + "(" + getDps() + " Schaden pro Sekunde)";
    }

    public double getDps() {

        return dps;
    }

    public double getEquipedDps() {

        return equipedDps;
    }

    public void setEquipedDps(double equipedDps) {

        ChatColor color = ChatColor.WHITE;
        this.equipedDps = equipedDps;
        if (getEquipedDps() < getDps()) {
            color = ChatColor.GREEN;
            // lineMessage = ChatColor.GREEN + "(" + getDps() + " [+" + (getDps() - getEquipedDps()) + "] Schaden pro Sekunde)";
        } else if (getEquipedDps() > getDps()) {
            color = ChatColor.RED;
            // lineMessage = ChatColor.RED + "(" + getDps() + " [-" + (getEquipedDps() - getDps()) + "] Schaden pro Sekunde)";
        }
        lineMessage = color + "(" + getDps() + " Schaden pro Sekunde)";
    }

    @Override
    protected void updateLineWidth() {

        setWidth(CustomItemUtil.getStringWidth(lineMessage));
    }

    @Override
    public String[] getTooltip() {

        return new String[]{lineMessage};
    }
}

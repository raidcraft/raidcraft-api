package de.raidcraft.api.items.tooltip;

import de.raidcraft.util.CustomItemUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

import java.util.regex.Pattern;

/**
 * @author Silthus
 */
public class DurabilityTooltip extends Tooltip {

    public static final Pattern DURABILITY_PATTERN = Pattern.compile("^Haltbarkeit: ([0-9]+)/([0-9]+)$");

    private int durability;
    private final int maxDurability;
    protected ChatColor color;

    public DurabilityTooltip(int durability, int maxDurability) {

        super(TooltipSlot.DURABILITY);
        this.durability = durability;
        this.maxDurability = maxDurability;
        color = ChatColor.GRAY;
        double durabilityInPercent = (double) durability / (double) maxDurability;
        if (durabilityInPercent < 0.10) {
            color = ChatColor.DARK_RED;
        } else if (durabilityInPercent < 0.20) {
            color = ChatColor.GOLD;
        }
        String tooltip = color + "Haltbarkeit: " + durability + "/" + maxDurability;
        setTooltip(tooltip);
        updateLineWidth(tooltip);
    }

    public int getDurability() {

        return durability;
    }

    public int getMaxDurability() {

        return maxDurability;
    }

    @Override
    protected void updateLineWidth(String... tooltip) {

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

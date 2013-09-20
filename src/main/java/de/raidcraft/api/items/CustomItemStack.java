package de.raidcraft.api.items;

import de.raidcraft.util.CustomItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Silthus
 */
public class CustomItemStack extends ItemStack {

    private static final Pattern DURABILITY_PATTERN = Pattern.compile("^Haltbarkeit: ([0-9]+)/([0-9]+)$");

    private final CustomItem item;
    private final Map<TooltipSlot, Tooltip> tooltips = new EnumMap<>(TooltipSlot.class);
    private int durability;

    protected CustomItemStack(CustomItem item, ItemStack itemStack) {

        super(itemStack);
        this.item = item;
        // lets add the item tooltips to our item stack
        tooltips.putAll(item.getTooltips());

        if (item instanceof CustomEquipment && ((CustomEquipment) item).getMaxDurability() > 0) {
            setCustomDurability(parseDurability());
        }
        // lets remove the vanilla attributes to clean up the display
        new Attributes(this).clear();
    }

    public int getCustomDurability() {

        return durability;
    }

    public void setCustomDurability(int durability) {

        if (durability < 1) {
            durability = 0;
        }
        this.durability = durability;
        // define the state of the item via color
        ChatColor color = ChatColor.GRAY;
        double durabilityInPercent = (double) durability / (double) getMaxDurability();
        if (durabilityInPercent < 0.10) {
            color = ChatColor.DARK_RED;
        } else if (durabilityInPercent < 0.20) {
            color = ChatColor.GOLD;
        }
        // lets reset the mc durability
        setDurability(CustomItemUtil.getMinecraftDurability(this, durability, getMaxDurability()));
        // set the new tooltip line
        setTooltip(new SingleLineTooltip(TooltipSlot.DURABILITY, color + "Haltbarkeit: " + durability + "/" + getMaxDurability()));
    }

    public int getMaxDurability() {

        if (getItem() instanceof CustomEquipment) {
            return ((CustomEquipment) getItem()).getMaxDurability();
        }
        return getCustomDurability();
    }

    public int parseDurability() {

        if (getItemMeta().hasLore()) {
            Matcher matcher;
            for (String line : getItemMeta().getLore()) {
                matcher = DURABILITY_PATTERN.matcher(ChatColor.stripColor(line));
                if (matcher.matches()) {
                    int durability = Integer.parseInt(matcher.group(1));
                    return durability < 1 ? 0 : durability;
                }
            }
        }
        return getMaxDurability();
    }

    public CustomItem getItem() {

        return item;
    }

    @Override
    public int getMaxStackSize() {

        return (getMetaDataId() > 0 ? 1 : getItem().getMaxStackSize());
    }

    protected void setTooltip(Tooltip tooltip) {

        this.tooltips.put(tooltip.getSlot(), tooltip);
    }

    public Tooltip getTooltip(TooltipSlot slot) {

        return tooltips.get(slot);
    }

    public boolean hasTooltip(TooltipSlot slot) {

        return tooltips.containsKey(slot);
    }

    public void setMetaDataId(int id) {

        setTooltip(new SingleLineTooltip(TooltipSlot.META_ID, CustomItemUtil.encodeItemId(id)));
        rebuild();
    }

    public int getMetaDataId() {

        try {
            if (hasTooltip(TooltipSlot.META_ID)) {
                return CustomItemUtil.decodeItemId(getTooltip(TooltipSlot.META_ID).getTooltip()[0]);
            }
        } catch (CustomItemException ignored) {
        }
        return -1;
    }

    public void rebuild() {

        ItemMeta itemMeta = getItemMeta();
        List<String> lore = new ArrayList<>();
        // lets go thru all registered tooltips and set them in order
        for (TooltipSlot slot : TooltipSlot.values()) {
            if (!hasTooltip(slot)) {
                continue;
            }
            if (slot == TooltipSlot.NAME) {
                itemMeta.setDisplayName(getTooltip(slot).getTooltip()[0]);
            } else {
                Collections.addAll(lore, getTooltip(slot).getTooltip());
            }
        }
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof CustomItemStack)) return false;

        CustomItemStack that = (CustomItemStack) o;

        return (getMetaDataId() > 0 ? that.getMetaDataId() == getMetaDataId() : item.equals(that.item));
    }

    @Override
    public CustomItemStack clone() {

        return new CustomItemStack(getItem(), super.clone());
    }
}

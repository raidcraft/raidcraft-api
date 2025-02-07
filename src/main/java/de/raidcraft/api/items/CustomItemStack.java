package de.raidcraft.api.items;

import com.sk89q.util.StringUtil;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.attachments.AttachableCustomItem;
import de.raidcraft.api.items.attachments.ItemAttachment;
import de.raidcraft.api.items.attachments.RequiredItemAttachment;
import de.raidcraft.api.items.tooltip.*;
import de.raidcraft.util.CustomItemUtil;
import de.raidcraft.util.ItemUtils;
import de.raidcraft.util.items.EnchantGlow;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * @author Silthus
 */
public class CustomItemStack extends ItemStack {

    private final CustomItem item;
    private final Map<TooltipSlot, Tooltip> tooltips = new EnumMap<>(TooltipSlot.class);
    private int durability;

    protected CustomItemStack(CustomItem item, ItemStack itemStack) {

        super(itemStack);
        this.item = item;
        // lets add the item tooltips to our item stack
        tooltips.putAll(item.getTooltips());
        // lets parse all existing tooltips of the itemstack
        tooltips.putAll(CustomItemUtil.parseTooltips(itemStack));

        if (item instanceof CustomEquipment && ((CustomEquipment) item).getMaxDurability() > 0) {
            setCustomDurability(parseDurability());
        }

        if (getItem().isEnchantmentEffect()) {
            EnchantGlow.addGlow(this);
        }
    }

    public int getCustomDurability() {

        return durability;
    }

    public void setCustomDurability(int durability) {

        if (durability < 1) {
            durability = 0;
        }
        this.durability = durability;
        // lets reset the mc durability
        setDurability(CustomItemUtil.getMinecraftDurability(this, durability, getMaxDurability()));
        if (getMaxDurability() > 0) {
            // set the new tooltip line
            setTooltip(new DurabilityTooltip(durability, getMaxDurability()));
        } else {
            removeTooltip(TooltipSlot.DURABILITY);
        }
    }

    public int getMaxDurability() {

        if (getItem() instanceof CustomEquipment) {
            return ((CustomEquipment) getItem()).getMaxDurability();
        }
        return getCustomDurability();
    }

    public int parseDurability() {

        if (hasTooltip(TooltipSlot.DURABILITY)) {
            return ((DurabilityTooltip) getTooltip(TooltipSlot.DURABILITY)).getDurability();
        }
        return getMaxDurability();
    }

    public boolean isSoulbound() {

        return getItem().getBindType() != ItemBindType.NONE
                && hasTooltip(TooltipSlot.BIND_TYPE)
                && ((BindTooltip) getTooltip(TooltipSlot.BIND_TYPE)).getOwner() != null;
    }

    public boolean isQuestItem() {
        return getItem().getType() == ItemType.QUEST;
    }

    public void setOwner(Player player) {

        try {
            setOwner(player != null ? player.getUniqueId() : null);
            rebuild(player);
        } catch (CustomItemException ignored) {
        }
    }

    public void setOwner(UUID owner) {

        if (owner == null) return;
        setTooltip(new BindTooltip(ItemBindType.SOULBOUND, owner));
    }

    public Optional<UUID> getOwner() {

        if (hasTooltip(TooltipSlot.BIND_TYPE)) {
            return Optional.ofNullable(((BindTooltip) getTooltip(TooltipSlot.BIND_TYPE)).getOwner());
        }
        return Optional.empty();
    }

    public CustomItem getItem() {

        return item;
    }

    @Override
    public int getMaxStackSize() {

        return (getMetaDataId() > 0 ? 1 : getItem().getMaxStackSize());
    }

    public void setTooltip(Tooltip tooltip) {

        this.tooltips.put(tooltip.getSlot(), tooltip);
    }

    public Tooltip getTooltip(TooltipSlot slot) {

        return tooltips.get(slot);
    }

    public boolean hasTooltip(TooltipSlot slot) {

        return tooltips.containsKey(slot);
    }

    public void removeTooltip(TooltipSlot slot) {

        tooltips.remove(slot);
    }

    public void setMetaDataId(int id) {

        if (id <= 0) {
            removeTooltip(TooltipSlot.META_ID);
        } else {
            setTooltip(new MetaDataTooltip(id));
        }
        rebuild();
    }

    public int getMetaDataId() {

        if (hasTooltip(TooltipSlot.META_ID)) {
            return ((MetaDataTooltip) getTooltip(TooltipSlot.META_ID)).getId();
        }
        return -1;
    }

    public Collection<ItemAttribute> getAttributes() {

        Collection<ItemAttribute> attributes = new ArrayList<>();
        if (getItem() instanceof AttributeHolder) {
            attributes = ((AttributeHolder) getItem()).getAttributes();
        }
        if (hasTooltip(TooltipSlot.ENCHANTMENTS)) {
            EnchantmentTooltip tooltip = (EnchantmentTooltip) getTooltip(TooltipSlot.ENCHANTMENTS);
            for (ItemAttribute attribute : tooltip.getAttributes()) {
                boolean exists = false;
                for (ItemAttribute existingAttribute : new ArrayList<>(attributes)) {
                    if (existingAttribute.getType() == attribute.getType()) {
                        attributes.remove(existingAttribute);
                        attributes.add(new ItemAttribute(existingAttribute.getType(), existingAttribute.getValue() + attribute.getValue()));
                        exists = true;
                        break;
                    }
                }
                if (!exists) attributes.add(attribute);
            }
        }
        return attributes;
    }

    public void rebuild(Player player) throws CustomItemException {

        for (ItemStack itemStack : player.getEquipment().getArmorContents()) {
            updateEquipedValue(itemStack);
        }
        updateWeaponEquipedValue(player);
        updateRequirements(player);
        rebuild();
    }

    private void updateRequirements(Player player) throws CustomItemException {

        if (!(getItem() instanceof AttachableCustomItem)) {
            return;
        }
        // lets also add our requirement lore
        for (ItemAttachment attachment : ((AttachableCustomItem) getItem()).getAttachments(player)) {
            if (attachment instanceof RequiredItemAttachment) {
                ((AttachableCustomItem) getItem()).apply(player, this, true);
                RequirementTooltip tooltip;
                if (hasTooltip(TooltipSlot.REQUIREMENT)) {
                    tooltip = (RequirementTooltip) getTooltip(TooltipSlot.REQUIREMENT);
                    tooltip.addRequirement((RequiredItemAttachment) attachment);
                } else {
                    tooltip = new RequirementTooltip((RequiredItemAttachment) attachment);
                    setTooltip(tooltip);
                }
                // set the tooltip color
                if (((RequiredItemAttachment) attachment).isRequirementMet(player)) {
                    tooltip.setColor(ChatColor.WHITE);
                } else {
                    tooltip.setColor(ChatColor.RED);
                }
            }
        }
    }

    private void updateDamagePerSecond(ItemStack itemStack) {

        if (!hasTooltip(TooltipSlot.DPS)) {
            return;
        }
        DPSTooltip tooltip = (DPSTooltip) getTooltip(TooltipSlot.DPS);
        CustomItemStack customItem = RaidCraft.getCustomItem(itemStack);
        if (customItem != null && customItem.hasTooltip(TooltipSlot.DPS)) {
            tooltip.setEquipedDps(((DPSTooltip) customItem.getTooltip(TooltipSlot.DPS)).getDps());
        }
    }

    private void updateWeaponEquipedValue(Player player) {

        if (getItem() instanceof CustomEquipment) {
            ItemStack itemStack;
            if (((CustomEquipment) getItem()).getEquipmentSlot() == EquipmentSlot.SHIELD_HAND) {
                itemStack = player.getInventory().getItem(1);
            } else {
                itemStack = player.getInventory().getItem(0);
            }
            updateEquipedValue(itemStack);
            updateDamagePerSecond(itemStack);
        }
    }

    private void updateEquipedValue(ItemStack itemStack) {

        if (!hasTooltip(TooltipSlot.ATTRIBUTES)) {
            return;
        }
        AttributeTooltip thisAttributes = (AttributeTooltip) getTooltip(TooltipSlot.ATTRIBUTES);
        // set the equiped item attributes
        CustomItemStack customItemStack = RaidCraft.getCustomItem(itemStack);
        if (customItemStack != null) {
            if (!(customItemStack.getItem() instanceof CustomEquipment) || !(getItem() instanceof CustomEquipment)) {
                return;
            }
            if (((CustomEquipment) customItemStack.getItem()).getEquipmentSlot() != ((CustomEquipment) getItem()).getEquipmentSlot()) {
                return;
            }
            AttributeTooltip tooltip = (AttributeTooltip) customItemStack.getTooltip(TooltipSlot.ATTRIBUTES);
            for (AttributeType type : AttributeType.values()) {
                if (!thisAttributes.hasAttribute(type)) {
                    continue;
                }
                ItemAttribute attribute = thisAttributes.getAttribute(type);
                if (tooltip.hasAttribute(type)) {
                    attribute.setEquipedValue(tooltip.getAttribute(type).getValue());
                } else {
                    attribute.setEquipedValue(0);
                }
            }
        } else {
            for (AttributeType type : AttributeType.values()) {
                if (thisAttributes.hasAttribute(type)) {
                    thisAttributes.getAttribute(type).setEquipedValue(0);
                }
            }
        }
    }

    private void updateMaxWidth() {

        int maxWidth = Tooltip.DEFAULT_WIDTH;
        for (TooltipSlot slot : TooltipSlot.values()) {
            if (!hasTooltip(slot) || slot == TooltipSlot.NAME) {
                continue;
            }
            if (getTooltip(slot).getWidth() > maxWidth) {
                maxWidth = getTooltip(slot).getWidth();
            }
        }
        if (maxWidth > Tooltip.DEFAULT_WIDTH) {
            for (TooltipSlot slot : TooltipSlot.values()) {
                if (!hasTooltip(slot) || slot == TooltipSlot.NAME) {
                    continue;
                }
                getTooltip(slot).setWidth(maxWidth);
            }
        }
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
        itemMeta.setLore(lore);
        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS);
        setItemMeta(itemMeta);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof CustomItemStack)) return false;

        CustomItemStack that = (CustomItemStack) o;

        return super.equals(o) && (!(getMetaDataId() > 0 && that.getMetaDataId() > 0)
                || that.getMetaDataId() == getMetaDataId()) && item.equals(that.item);
    }

    @Override
    public CustomItemStack clone() {

        return new CustomItemStack(getItem(), super.clone());
    }

    @Override
    public String toString() {

        return "CustomItemStack{" +
                "item=" + item.toString() +
                ", tooltips=[" + StringUtil.joinString(tooltips.values(), ",", 0) + "]" +
                '}';
    }
}

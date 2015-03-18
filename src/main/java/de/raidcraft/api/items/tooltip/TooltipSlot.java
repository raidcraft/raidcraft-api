package de.raidcraft.api.items.tooltip;

/**
 * @author Silthus
 */
public enum TooltipSlot {

    NAME(TooltipSlotType.SINGLE),
    ITEM_LEVEL(TooltipSlotType.SINGLE),
    BIND_TYPE(TooltipSlotType.SINGLE),
    EQUIPMENT_TYPE(TooltipSlotType.SINGLE),
    ARMOR(TooltipSlotType.SINGLE),
    DAMAGE(TooltipSlotType.SINGLE),
    DPS(TooltipSlotType.SINGLE),
    ATTRIBUTES(TooltipSlotType.FIXED_MULTI_LINE),
    LORE(TooltipSlotType.FIXED_MULTI_LINE),
    ATTACHMENT(TooltipSlotType.FIXED_MULTI_LINE),
    SPACER(TooltipSlotType.SINGLE),
    REQUIREMENT(TooltipSlotType.FIXED_MULTI_LINE),
    SELL_PRICE(TooltipSlotType.SINGLE),
    DURABILITY(TooltipSlotType.SINGLE),
    MISC(TooltipSlotType.FIXED_MULTI_LINE),
    META_ID(TooltipSlotType.SINGLE);

    private final TooltipSlotType lineType;

    private TooltipSlot(TooltipSlotType lineType) {

        this.lineType = lineType;
    }

    public TooltipSlotType getLineType() {

        return lineType;
    }
}

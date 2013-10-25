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
    ATTRIBUTES(TooltipSlotType.MULTI_LINE),
    LORE(TooltipSlotType.MULTI_LINE),
    ATTACHMENT(TooltipSlotType.MULTI_LINE),
    SPACER(TooltipSlotType.SINGLE),
    REQUIREMENT(TooltipSlotType.MULTI_LINE),
    SELL_PRICE(TooltipSlotType.SINGLE),
    DURABILITY(TooltipSlotType.SINGLE),
    META_ID(TooltipSlotType.SINGLE),
    MISC(TooltipSlotType.MULTI_LINE);

    private final TooltipSlotType lineType;

    private TooltipSlot(TooltipSlotType lineType) {

        this.lineType = lineType;
    }

    public TooltipSlotType getLineType() {

        return lineType;
    }
}

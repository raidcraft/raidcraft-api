package de.raidcraft.api.items.tooltip;

/**
 * @author Silthus
 */
public enum TooltipSlot {

    NAME(0, TooltipSlotType.SINGLE),
    ITEM_LEVEL(1, TooltipSlotType.SINGLE),
    BIND_TYPE(2, TooltipSlotType.SINGLE),
    EQUIPMENT_TYPE(3, TooltipSlotType.SINGLE),
    ARMOR(4, TooltipSlotType.SINGLE),
    DAMAGE(5, TooltipSlotType.SINGLE),
    DPS(6, TooltipSlotType.SINGLE),
    ATTRIBUTES(7, TooltipSlotType.FIXED_MULTI_LINE),
    SOCKETS(16, TooltipSlotType.FIXED_MULTI_LINE),
    ENCHANTMENTS(17, TooltipSlotType.FIXED_MULTI_LINE),
    LORE(8, TooltipSlotType.FIXED_MULTI_LINE),
    ATTACHMENT(9, TooltipSlotType.FIXED_MULTI_LINE),
    CONSUMEABLE(18, TooltipSlotType.VARIABLE_MULTI_LINE),
    SPACER(10, TooltipSlotType.SINGLE),
    REQUIREMENT(11, TooltipSlotType.FIXED_MULTI_LINE),
    SELL_PRICE(12, TooltipSlotType.SINGLE),
    DURABILITY(13, TooltipSlotType.SINGLE),
    MISC(14, TooltipSlotType.FIXED_MULTI_LINE),
    TYPE(15, TooltipSlotType.SINGLE),
    META_ID(16, TooltipSlotType.SINGLE);

    private final int id;
    private final TooltipSlotType lineType;

    TooltipSlot(int id, TooltipSlotType lineType) {

        this.id = id;
        this.lineType = lineType;
    }

    public int getId() {

        return id;
    }

    public TooltipSlotType getLineType() {

        return lineType;
    }

    public static TooltipSlot fromId(int id) {

        for (TooltipSlot slot : values()) {
            if (slot.id == id) return slot;
        }
        return null;
    }
}

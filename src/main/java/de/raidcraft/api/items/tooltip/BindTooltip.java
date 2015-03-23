package de.raidcraft.api.items.tooltip;

import de.raidcraft.api.items.ItemBindType;
import de.raidcraft.util.CustomItemUtil;
import de.raidcraft.util.UUIDUtil;

import java.util.UUID;

/**
 * @author mdoering
 */
public class BindTooltip extends SingleLineTooltip {

    private ItemBindType bindType;
    private UUID owner;

    public BindTooltip(ItemBindType bindType, UUID owner) {

        super(TooltipSlot.BIND_TYPE, bindType.getItemText(), bindType.getColor());
        this.owner = owner;
        this.bindType = owner != null ? ItemBindType.SOULBOUND : bindType;
        setTooltip(CustomItemUtil.encodeItemId(UUIDUtil.getPlayerId(owner)) + bindType.getColor() + bindType.getItemText());
    }

    public UUID getOwner() {

        return owner;
    }

    public void setOwner(UUID owner) {

        this.owner = owner;
        setTooltip(CustomItemUtil.encodeItemId(UUIDUtil.getPlayerId(owner)) + bindType.getColor() + bindType.getItemText());
    }
}

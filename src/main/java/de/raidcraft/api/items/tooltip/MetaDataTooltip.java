package de.raidcraft.api.items.tooltip;

import de.raidcraft.util.CustomItemUtil;

/**
 * @author Silthus
 */
public class MetaDataTooltip extends Tooltip {

    private final int id;

    public MetaDataTooltip(int id) {

        super(TooltipSlot.META_ID);
        this.id = id;
        String itemId = CustomItemUtil.encodeItemId(id);
        setTooltip(itemId);
    }

    public int getId() {

        return id;
    }

    @Override
    protected void updateLineWidth() {

    }
}

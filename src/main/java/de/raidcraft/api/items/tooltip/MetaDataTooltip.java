package de.raidcraft.api.items.tooltip;

import de.raidcraft.util.CustomItemUtil;

/**
 * @author Silthus
 */
public class MetaDataTooltip extends Tooltip {

    private String[] text;

    public MetaDataTooltip(int id) {

        super(TooltipSlot.META_ID);
        String itemId = CustomItemUtil.encodeItemId(id);
        text = new String[]{itemId};
    }

    @Override
    protected void updateLineWidth() {

    }

    @Override
    public String[] getTooltip() {

        return text;
    }
}

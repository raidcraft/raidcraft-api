package de.raidcraft.api.items.tooltip;

import de.raidcraft.RaidCraft;
import de.raidcraft.util.CustomItemUtil;

/**
 * @author Silthus
 */
public class MetaDataTooltip extends Tooltip {

    private String[] text;

    public MetaDataTooltip(int id) {

        super(TooltipSlot.META_ID);
        String itemId = CustomItemUtil.encodeItemId(id);
        RaidCraft.LOGGER.info("Encoded meta data id from " + id + " to " + itemId);
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

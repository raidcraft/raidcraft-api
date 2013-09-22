package de.raidcraft.api.items.tooltip;

import de.raidcraft.api.items.AttributeType;
import de.raidcraft.api.items.ItemAttribute;
import de.raidcraft.util.CustomItemUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public class AttributeTooltip extends Tooltip {

    private final Map<AttributeType, ItemAttribute> attributes = new HashMap<>();

    public AttributeTooltip(Collection<ItemAttribute> attributes) {

        super(TooltipSlot.ATTRIBUTES);
        // we need to sort the attributes and make them nicer
        for (ItemAttribute attribute : attributes) {
            try {
                this.attributes.put(attribute.getType(), attribute.clone());
            } catch (CloneNotSupportedException ignored) {

            }
        }
    }

    public Collection<ItemAttribute> getAttributes() {

        return attributes.values();
    }

    public ItemAttribute getAttribute(AttributeType type) {

        return attributes.get(type);
    }

    public boolean hasAttribute(AttributeType type) {

        return attributes.containsKey(type);
    }

    @Override
    protected void updateLineWidth() {

        for (ItemAttribute attribute : attributes.values()) {
            int width = CustomItemUtil.getStringWidth(attribute.getItemLine());
            if (width > getWidth()) {
                setWidth(width);
            }
        }
    }

    @Override
    public String[] getTooltip() {

        List<ItemAttribute> attributes = new ArrayList<>(this.attributes.values());
        Collections.sort(attributes);
        String[] array = new String[attributes.size()];
        for (int i = 0; i < attributes.size(); i++) {
            array[i] = attributes.get(i).getItemLine();
        }
        return array;
    }
}

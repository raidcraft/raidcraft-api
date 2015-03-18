package de.raidcraft.api.items.tooltip;

import de.raidcraft.api.items.AttributeDisplayType;
import de.raidcraft.api.items.AttributeType;
import de.raidcraft.api.items.ItemAttribute;
import de.raidcraft.util.CustomItemUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        setTooltip(buildTooltip());
    }

    private String[] buildTooltip() {

        List<ItemAttribute> attributes = new ArrayList<>(this.attributes.values());
        String[] array = new String[attributes.size()];
        List<ItemAttribute> addLater = attributes.stream()
                .filter(attribute -> attribute.getType().getDisplayType() == AttributeDisplayType.BELOW)
                .collect(Collectors.toList());
        attributes.removeAll(addLater);
        Collections.sort(attributes);
        Collections.sort(addLater);
        int i = 0;
        for (ItemAttribute attribute : attributes) {
            array[i] = attribute.getItemLine();
            i++;
        }
        for (ItemAttribute attribute : addLater) {
            array[i] = attribute.getItemLine();
            i++;
        }
        return array;
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
}

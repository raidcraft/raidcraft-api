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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public class AttributeTooltip extends Tooltip {

    public static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("^([\\+-])([0-9]+) (\\w+).*$");

    private final Map<AttributeType, ItemAttribute> attributes = new HashMap<>();

    public AttributeTooltip(Collection<ItemAttribute> attributes) {

        this(TooltipSlot.ATTRIBUTES, attributes.toArray(new ItemAttribute[attributes.size()]));
    }

    public AttributeTooltip(TooltipSlot slot, ItemAttribute... attributes) {

        super(slot);
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
    protected void updateLineWidth(String... lines) {

        for (ItemAttribute attribute : attributes.values()) {
            int width = CustomItemUtil.getStringWidth(attribute.getItemLine());
            if (width > getWidth()) {
                setWidth(width);
            }
        }
    }
}

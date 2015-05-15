package de.raidcraft.api.items;

import java.util.Collection;

/**
 * @author mdoering
 */
public interface AttributeHolder {

    Collection<ItemAttribute> getAttributes();

    void addAttribute(ItemAttribute attribute);

    ItemAttribute removeAttribute(AttributeType attribute);
}

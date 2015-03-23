package de.raidcraft.api.items;

import java.util.Collection;

/**
 * @author mdoering
 */
public interface AttributeHolder {

    public Collection<ItemAttribute> getAttributes();

    public void addAttribute(ItemAttribute attribute);

    public ItemAttribute removeAttribute(AttributeType attribute);
}

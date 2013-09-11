package de.raidcraft.util;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Silthus
 */
public class CaseInsensitiveSet extends HashSet<String> {

    public CaseInsensitiveSet(Collection<? extends String> c) {

        super(c.size());
        addAll(c);
    }

    public CaseInsensitiveSet(int initialCapacity, float loadFactor) {

        super(initialCapacity, loadFactor);
    }

    public CaseInsensitiveSet(int initialCapacity) {

        super(initialCapacity);
    }

    @Override
    public boolean add(String  e) {

        return super.add(e.toLowerCase());
    }

    @Override
    public boolean remove(Object o) {

        return super.remove(o == null ? null : o.toString().toLowerCase());
    }

    @Override
    public boolean contains(Object o) {

        return super.contains(o == null ? null : o.toString().toLowerCase());
    }
}

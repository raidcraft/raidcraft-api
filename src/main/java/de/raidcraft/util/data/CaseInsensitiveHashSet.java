package de.raidcraft.util.data;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Silthus
 */
public class CaseInsensitiveHashSet extends HashSet<String> {

    public CaseInsensitiveHashSet() {

        super();
    }

    public CaseInsensitiveHashSet(Collection<? extends String> c) {

        super(c.size());
        addAll(c);
    }

    public CaseInsensitiveHashSet(int initialCapacity, float loadFactor) {

        super(initialCapacity, loadFactor);
    }

    public CaseInsensitiveHashSet(int initialCapacity) {

        super(initialCapacity);
    }

    @Override
    public boolean add(String e) {

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

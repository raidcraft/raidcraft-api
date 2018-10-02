package de.raidcraft.api.random;

import lombok.Data;

@Data
public final class NamedRDSTable {

    private final String name;
    private final RDSTable table;
}

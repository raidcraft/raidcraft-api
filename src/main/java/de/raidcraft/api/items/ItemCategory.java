package de.raidcraft.api.items;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(of = "name")
@RequiredArgsConstructor
public class ItemCategory {

    private final String name;
    private String description;
}

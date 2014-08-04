package de.raidcraft.tables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import java.util.UUID;

/**
 * @author Dragonfire
 */
@Getter
@Setter
public class TPlayer {

    @Id
    private UUID uuid;
    private String lastName;
}

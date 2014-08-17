package de.raidcraft.tables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * @author Dragonfire
 */
@Getter
@Setter
@Entity
@Table(name = "rc_players")
public class TRcPlayer {

    @Id
    private UUID uuid;
    private String lastName;
}

package de.raidcraft.tables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Dragonfire
 */
@Entity
@Table(name = "rc_actionapi")
public class TActionApi {

    @Getter
    @Setter
    @Id
    private int id;
    @Getter
    @Setter
    private String action_type;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String description;
}
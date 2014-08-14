package de.raidcraft.tables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Dragonfire
 */
@Getter
@Setter
@Entity
@Table(name = "rc_actionapi")
public class TActionApi {


    @Id
    private int id;
    private String action_type;
    private String name;
    private String description;
}
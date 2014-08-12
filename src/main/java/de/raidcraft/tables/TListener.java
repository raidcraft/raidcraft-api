package de.raidcraft.tables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Dragonfire
 */
@Getter
@Setter
@Entity
@Table(name = "rc_listener")
public class TListener {


    @Id
    private String listener;
    private String plugin;
    private Date lastLoaded = new Date();
}

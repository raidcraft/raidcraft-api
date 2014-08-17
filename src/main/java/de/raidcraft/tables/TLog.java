package de.raidcraft.tables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
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
@Table(name = "rc_log")
public class TLog {

    @Id
    private int id;
    private RcLogLeevel level;
    private String category;
    @Column(columnDefinition = "TEXT")
    private String log;
    private Date last;
    private String server;
}
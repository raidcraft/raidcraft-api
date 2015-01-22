package de.raidcraft.tables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "rc_plugin")
public class TPlugin_ {

    @Id
    private int id;
    private String name;
    private String version;
    private String author;
    private Date lastActive;
}

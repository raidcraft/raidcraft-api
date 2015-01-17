package de.raidcraft.api.storage;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "rc_object_storage")
public class TObjectStorage {

    @Id
    private int id;
    @NotNull
    private String storageName;
    @NotNull
    @Column(length = 16384)
    private String serialization;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getStorageName() {

        return storageName;
    }

    public void setStorageName(String storageName) {

        this.storageName = storageName;
    }

    public String getSerialization() {

        return serialization;
    }

    public void setSerialization(String serialization) {

        this.serialization = serialization;
    }
}

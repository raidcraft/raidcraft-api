package de.raidcraft.api.ebean;

import io.ebean.EbeanServer;
import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.time.Instant;

@Data
@MappedSuperclass
public abstract class BaseModel extends Model {

    @Id
    long id;

    @Version
    Long version;

    @WhenCreated
    Instant whenCreated;

    @WhenModified
    Instant whenModified;

    protected abstract EbeanServer database();

    @Override
    public void markAsDirty() {
        database().markAsDirty(this);
    }

    @Override
    public void save() {
        database().save(this);
    }

    @Override
    public void flush() {
        database().flush();
    }

    @Override
    public void update() {
        database().update(this);
    }

    @Override
    public void insert() {
        database().insert(this);
    }

    @Override
    public boolean delete() {
        return database().delete(this);
    }

    @Override
    public boolean deletePermanent() {
        return database().deletePermanent(this);
    }

    @Override
    public void refresh() {
        database().refresh(this);
    }
}

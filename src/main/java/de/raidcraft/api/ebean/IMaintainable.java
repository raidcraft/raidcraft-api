package de.raidcraft.api.ebean;

import de.raidcraft.api.BasePlugin;

/**
 * Basic CRUD functionality.
 */
public interface IMaintainable {

    void save();

    void save(Class<? extends BasePlugin> clazz);

    void update();

    void update(Class<? extends BasePlugin> clazz);

    void delete();

    void delete(Class<? extends BasePlugin> clazz);

    void refresh();

    void refresh(final Class<? extends BasePlugin> clazz);
}

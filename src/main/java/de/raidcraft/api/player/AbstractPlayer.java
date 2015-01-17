package de.raidcraft.api.player;

import de.raidcraft.RaidCraft;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public abstract class AbstractPlayer implements RCPlayer {

    private final Map<Class<? extends PlayerComponent>, PlayerComponent> components = new HashMap<>();

    private final String username;
    private String displayName;

    protected AbstractPlayer(String username) {

        this.username = username;
        this.displayName = username;
    }

    @Override
    public final <T extends PlayerComponent> T getComponent(Class<T> clazz) {

        PlayerComponent component = null;
        if (components.containsKey(clazz)) {
            component = components.get(clazz);
        } else {
            try {
                // lets check if the clazz has a constructor taking RCPlayer as argument
                Constructor<T> constructor = clazz.getConstructor(RCPlayer.class);
                // lets set it accessible if private
                constructor.setAccessible(true);
                // create the instance
                component = constructor.newInstance(this);
                // and attach it to our RCPlayer object cache
                components.put(clazz, component);
            } catch (ReflectiveOperationException e) {
                RaidCraft.LOGGER.severe(e.getMessage());
                e.printStackTrace();
            }
        }
        return clazz.cast(component);
    }

    @Override
    public String getUserName() {

        return username;
    }

    @Override
    public String getDisplayName() {

        return displayName;
    }

    @Override
    public boolean isInWorld(String world) {

        return world.equalsIgnoreCase(getWorld());
    }

    @Override
    public String toString() {

        return username;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractPlayer that = (AbstractPlayer) o;

        return username.equals(that.username);

    }

    @Override
    public int hashCode() {

        return username.hashCode();
    }
}

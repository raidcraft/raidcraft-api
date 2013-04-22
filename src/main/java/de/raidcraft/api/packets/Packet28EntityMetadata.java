package de.raidcraft.api.packets;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;

/**
 * @author Silthus
 */
public class Packet28EntityMetadata extends AbstractPacket {

    public static final int ID = 40;

    public Packet28EntityMetadata() {

        super(new PacketContainer(ID), ID);
        handle.getModifier().writeDefaults();
    }

    public Packet28EntityMetadata(PacketContainer packet) {

        super(packet, ID);
    }

    /**
     * Retrieve unique entity ID to update.
     *
     * @return The current Entity ID
     */
    public int getEntityId() {

        return handle.getIntegers().read(0);
    }

    /**
     * Set unique entity ID to update.
     *
     * @param value - new value.
     */
    public void setEntityId(int value) {

        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve the entity.
     *
     * @param world - the current world of the entity.
     *
     * @return The entity.
     */
    public Entity getEntity(World world) {

        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the entity.
     *
     * @param event - the packet event.
     *
     * @return The entity.
     */
    public Entity getEntity(PacketEvent event) {

        return getEntity(event.getPlayer().getWorld());
    }

    /**
     * Retrieve a list of all the watchable objects.
     * <p/>
     * This can be converted to a data watcher using {@link com.comphenix.protocol.wrappers.WrappedDataWatcher#WrappedDataWatcher(List) WrappedDataWatcher(List)}
     *
     * @return The current metadata
     */
    public List<WrappedWatchableObject> getEntityMetadata() {

        return handle.getWatchableCollectionModifier().read(0);
    }

    /**
     * Set the list of the watchable objects (meta data).
     *
     * @param value - new value.
     */
    public void setEntityMetadata(List<WrappedWatchableObject> value) {

        handle.getWatchableCollectionModifier().write(0, value);
    }
}


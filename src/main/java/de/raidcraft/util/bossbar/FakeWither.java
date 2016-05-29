package de.raidcraft.util.bossbar;

import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

// You could also use a full-fledged API like RemoteEntities
public class FakeWither {

    public static final byte INVISIBLE = 0x20;
    public static final int DEFAULT_HEALTH = 300;
    // Just a guess
    private static final int HEALTH_RANGE = 80 * 80;
    // Next entity ID
    private static int NEXT_ID = 6000;

    private static final int METADATA_WITHER_HEALTH = 6; // 1.5.2 -> Change to 16

    // Metadata indices
    private static final int METADATA_FLAGS = 0;
    private static final int METADATA_NAME = 2;        // 1.5.2 -> Change to 5
    private static final int METADATA_SHOW_NAME = 3;   // 1.5.2 -> Change to 6

    // Unique ID
    private int id = NEXT_ID++;
    // Default health
    private int health = DEFAULT_HEALTH;

    private boolean visible;
    private String customName;
    private boolean created;

    private Location location;
    private ProtocolManager manager;

    public FakeWither(Location location, ProtocolManager manager) {

        this.location = location;
        this.manager = manager;
    }

    public int getHealth() {

        return health;
    }

    public void setHealth(int health) {
        // Update the health of the entity
        if (created) {
            WrappedDataWatcher watcher = new WrappedDataWatcher();

            watcher.setObject(METADATA_WITHER_HEALTH, (float) health); // 1.5.2 -> Change to (int)
            sendMetadata(watcher);
        }
        this.health = health;
    }

    public void setVisible(boolean visible) {
        // Make visible or invisible
        if (created) {
            WrappedDataWatcher watcher = new WrappedDataWatcher();

            watcher.setObject(METADATA_FLAGS, visible ? (byte) 0 : INVISIBLE);
            sendMetadata(watcher);
        }
        this.visible = visible;
    }

    public void setCustomName(String name) {

        if (created) {
            WrappedDataWatcher watcher = new WrappedDataWatcher();

            if (name != null) {
                watcher.setObject(METADATA_NAME, name);
                watcher.setObject(METADATA_SHOW_NAME, (byte) 1);
            } else {
                // Hide custom name
                watcher.setObject(METADATA_SHOW_NAME, (byte) 0);
            }

            // Only players nearby when this is sent will see this name
            sendMetadata(watcher);
        }
        this.customName = name;
    }

    private void sendMetadata(WrappedDataWatcher watcher) {

        WrapperPlayServerEntityMetadata update = new WrapperPlayServerEntityMetadata();

        update.setEntityID(id);
        update.setMetadata(watcher.getWatchableObjects());
        broadcastPacket(update.getHandle(), true);
    }

    public int getId() {

        return id;
    }

    public void create() {

        WrapperPlayServerSpawnEntityLiving spawnMob = new WrapperPlayServerSpawnEntityLiving();
        WrappedDataWatcher watcher = new WrappedDataWatcher();

        watcher.setObject(METADATA_FLAGS, visible ? (byte) 0 : INVISIBLE);
        watcher.setObject(METADATA_WITHER_HEALTH, (float) health); // 1.5.2 -> Change to (int)

        if (customName != null) {
            watcher.setObject(METADATA_NAME, customName);
            watcher.setObject(METADATA_SHOW_NAME, (byte) 1);
        }

        spawnMob.setEntityID(id);
        spawnMob.setType(EntityType.WITHER);
        spawnMob.setX(location.getX());
        spawnMob.setY(location.getY());
        spawnMob.setZ(location.getZ());
        spawnMob.setMetadata(watcher);

        broadcastPacket(spawnMob.getHandle(), true);
        created = true;
    }

    public void destroy() {

        if (!created) {
            throw new IllegalStateException("Cannot kill a killed entity.");
        }

        WrapperPlayServerEntityDestroy destroyMe = new WrapperPlayServerEntityDestroy();
        destroyMe.setEntityIds(new int[]{id});

        broadcastPacket(destroyMe.getHandle(), false);
        created = false;
    }

    private void broadcastPacket(PacketContainer packet, boolean onlyNearby) {

        // Must be within the range
        Bukkit.getServer().getOnlinePlayers().stream()
                .filter(player -> !onlyNearby || player.getLocation().distanceSquared(location) < HEALTH_RANGE).forEach(player -> {
            try {
                manager.sendServerPacket(player, packet);
            } catch (InvocationTargetException e) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot send " + packet + " to " + player, e);
            }
        });
    }
}
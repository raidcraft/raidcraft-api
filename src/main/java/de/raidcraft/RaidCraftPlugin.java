package de.raidcraft;

import com.avaje.ebean.Ebean;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.commands.ConfirmCommand;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.database.Bean;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public class RaidCraftPlugin extends BasePlugin implements Component {

    private static RaidCraftPlugin instance;
    public static RaidCraftPlugin getInstance() {

        return instance;
    }

    private LocalConfiguration config;
    private final Map<PlayerPlacedBlock, PlayerPlacedBlock> playerPlacedBlocks = new HashMap<>();

    public RaidCraftPlugin() {

        instance = this;
    }

    @Override
    public void enable() {

        this.config = configure(new LocalConfiguration(this), true);
        registerEvents(new RaidCraft());
        registerCommands(ConfirmCommand.class);
        RaidCraft.registerComponent(RaidCraftPlugin.class, this);

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        playerPlacedBlocks.clear();
        // lets load all player placed blocks
        Set<PlayerPlacedBlock> set = Ebean.find(PlayerPlacedBlock.class).findSet();
        for (PlayerPlacedBlock coordinate : set) {
            playerPlacedBlocks.put(coordinate, coordinate);
        }
    }

    @Override
    public void disable() {

        // lets save all player placed blocks
        Ebean.save(playerPlacedBlocks.values());
    }

    public static class LocalConfiguration extends ConfigurationBase<RaidCraftPlugin> {

        public LocalConfiguration(RaidCraftPlugin plugin) {

            super(plugin, "config.yml");
        }

        @Setting("player-placed-block-worlds")
        public List<String> player_placed_block_worlds = new ArrayList<>();
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> classes = new ArrayList<>();
        classes.add(PlayerPlacedBlock.class);
        return classes;
    }

    public void setPlayerPlaced(Block block) {

        if (!config.player_placed_block_worlds.contains(block.getWorld().getName())) {
            return;
        }
        PlayerPlacedBlock playerPlacedBlock = new PlayerPlacedBlock(block);
        if (!playerPlacedBlocks.containsKey(playerPlacedBlock)) {
            playerPlacedBlocks.put(playerPlacedBlock, playerPlacedBlock);
        }
    }

    public boolean isPlayerPlaced(Block block) {

        if (!config.player_placed_block_worlds.contains(block.getWorld().getName())) {
            return false;
        }
        PlayerPlacedBlock playerPlacedBlock = new PlayerPlacedBlock(block);
        return playerPlacedBlocks.containsKey(playerPlacedBlock);
    }

    public void removePlayerPlaced(Block block) {

        if (!config.player_placed_block_worlds.contains(block.getWorld().getName())) {
            return;
        }
        PlayerPlacedBlock playerPlacedBlock = new PlayerPlacedBlock(block);
        PlayerPlacedBlock remove = playerPlacedBlocks.remove(playerPlacedBlock);
        Ebean.delete(remove);
    }

    @Entity
    @Table(name = "rc_player_placed_blocks")
    public static class PlayerPlacedBlock implements Bean {

        @Id
        private int id;
        private String world;
        private int x;
        private int y;
        private int z;

        public PlayerPlacedBlock(Block block) {

            this(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
        }

        public PlayerPlacedBlock(String world, int x, int y, int z) {

            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public int getId() {

            return id;
        }

        public void setId(int id) {

            this.id = id;
        }

        public String getWorld() {

            return world;
        }

        public void setWorld(String world) {

            this.world = world;
        }

        public int getX() {

            return x;
        }

        public void setX(int x) {

            this.x = x;
        }

        public int getY() {

            return y;
        }

        public void setY(int y) {

            this.y = y;
        }

        public int getZ() {

            return z;
        }

        public void setZ(int z) {

            this.z = z;
        }

        @Override
        public boolean equals(Object o) {

            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PlayerPlacedBlock that = (PlayerPlacedBlock) o;

            return x == that.x && y == that.y && z == that.z && world.equals(that.world);
        }

        @Override
        public int hashCode() {

            int result = world.hashCode();
            result = 31 * result + x;
            result = 31 * result + y;
            result = 31 * result + z;
            return result;
        }
    }
}

package de.raidcraft;

import com.avaje.ebean.Ebean;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.commands.ConfirmCommand;
import de.raidcraft.api.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public class RaidCraftPlugin extends BasePlugin implements Component {

    private static RaidCraftPlugin instance;

    public static RaidCraftPlugin getInstance() {

        return instance;
    }

    private final Map<Block, PlayerPlacedBlock> playerPlacedBlocks = new HashMap<>();

    public RaidCraftPlugin() {

        instance = this;
    }

    @Override
    public void enable() {

        registerEvents(new RaidCraft());
        registerCommands(ConfirmCommand.class);
        RaidCraft.registerComponent(RaidCraftPlugin.class, this);
        // lets load all blocks that are player placed
        for (PlayerPlacedBlock block : Ebean.find(PlayerPlacedBlock.class).findSet()) {
            playerPlacedBlocks.put(block.getBlock(), block);
        }

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> classes = new ArrayList<>();
        classes.add(PlayerPlacedBlock.class);
        return classes;
    }

    @Override
    public void disable() {

        for (PlayerPlacedBlock block : playerPlacedBlocks.values()) {
            Database.save(block);
        }
    }

    public boolean isPlayerPlaced(Block block) {

        return playerPlacedBlocks.containsKey(block);
    }

    public void setPlayerPlaced(Block block) {

        playerPlacedBlocks.put(block, PlayerPlacedBlock.create(block));
    }

    public void removePlayerPlaced(Block block) {

        PlayerPlacedBlock remove = playerPlacedBlocks.remove(block);
        if (remove == null) return;
        Ebean.delete(remove);
    }
}

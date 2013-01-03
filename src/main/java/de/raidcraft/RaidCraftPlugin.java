package de.raidcraft;

import com.avaje.ebean.Ebean;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.commands.ConfirmCommand;
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

        registerCommands(ConfirmCommand.class);
        RaidCraft.registerComponent(RaidCraftPlugin.class, this);
        // lets load all blocks that are player placed
        for (PlayerPlacedBlock block : Ebean.find(PlayerPlacedBlock.class).findSet()) {
            playerPlacedBlocks.put(block.getBlock(), block);
        }
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> classes = new ArrayList<>();
        classes.add(PlayerPlacedBlock.class);
        return classes;
    }

    @Override
    public void disable() {

        Ebean.save(playerPlacedBlocks.values());
    }

    public boolean isPlayerPlaced(Block block) {

        return playerPlacedBlocks.containsKey(block);
    }

    public void setPlayerPlaced(Block block) {

        playerPlacedBlocks.put(block, new PlayerPlacedBlock(block));
    }

    public void removePlayerPlaced(Block block) {

        PlayerPlacedBlock remove = playerPlacedBlocks.remove(block);
        Ebean.delete(remove);
    }
}

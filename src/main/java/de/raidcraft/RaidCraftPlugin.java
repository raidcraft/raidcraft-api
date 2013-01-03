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

    private final Map<Block, Integer> playerPlacedBlocks = new HashMap<>();

    public RaidCraftPlugin() {

        instance = this;
    }

    @Override
    public void enable() {

        registerCommands(ConfirmCommand.class);
        RaidCraft.registerComponent(RaidCraftPlugin.class, this);
        // lets load all blocks that are player placed
        for (PlayerPlacedBlock block : Ebean.find(PlayerPlacedBlock.class).findSet()) {
            playerPlacedBlocks.put(block.getBlock(), block.getId());
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

        // lets save all player placed blocks that have an id if 0
        List<PlayerPlacedBlock> toSave = new ArrayList<>();
        for (Map.Entry<Block, Integer> entry : playerPlacedBlocks.entrySet()) {
            if (entry.getValue() == 0) {
                toSave.add(new PlayerPlacedBlock(entry.getKey()));
            }
        }
        Ebean.save(toSave);
    }

    public boolean isPlayerPlaced(Block block) {

        return playerPlacedBlocks.containsKey(block);
    }

    public void setPlayerPlaced(Block block) {

        playerPlacedBlocks.put(block, 0);
    }

    public void removePlayerPlaced(Block block) {

        int id = playerPlacedBlocks.remove(block);
        PlayerPlacedBlock placedBlock = Ebean.find(PlayerPlacedBlock.class, id);
        if (placedBlock != null) placedBlock.remove();
    }
}

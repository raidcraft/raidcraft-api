package de.raidcraft;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.commands.ConfirmCommand;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

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
        Set<PlayerPlacedBlock> set = getDatabase().find(PlayerPlacedBlock.class).findSet();
        for (PlayerPlacedBlock coordinate : set) {
            playerPlacedBlocks.put(coordinate, coordinate);
        }
    }

    @Override
    public void disable() {

        getDatabase().save(playerPlacedBlocks.values());
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
        if (remove.getId() > 1) {
            getDatabase().delete(remove);
        }
    }

}

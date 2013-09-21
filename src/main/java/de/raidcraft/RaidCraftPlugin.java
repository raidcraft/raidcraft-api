package de.raidcraft;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.commands.ConfirmCommand;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.inventory.InventoryManager;
import de.raidcraft.api.inventory.TPersistentInventory;
import de.raidcraft.api.inventory.TPersistentInventorySlot;
import de.raidcraft.api.items.CustomItemManager;
import de.raidcraft.api.items.attachments.ItemAttachmentManager;
import de.raidcraft.api.storage.TObjectStorage;
import de.raidcraft.util.AttributeHider;
import de.raidcraft.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public class RaidCraftPlugin extends BasePlugin implements Component, Listener {

    private static RaidCraftPlugin instance;
    public static RaidCraftPlugin getInstance() {

        return instance;
    }

    private LocalConfiguration config;
    private AttributeHider attributeHider;
    private final Map<Chunk, Set<PlayerPlacedBlock>> playerPlacedBlocks = new HashMap<>();
    private boolean started = false;

    public RaidCraftPlugin() {

        instance = this;
    }

    @Override
    public void enable() {

        this.config = configure(new LocalConfiguration(this), true);
        registerEvents(this);
        registerEvents(new RaidCraft());
        registerCommands(ConfirmCommand.class);
        RaidCraft.registerComponent(CustomItemManager.class, new CustomItemManager());
        RaidCraft.registerComponent(ItemAttachmentManager.class, new ItemAttachmentManager());
        RaidCraft.registerComponent(InventoryManager.class, new InventoryManager(this));

        if (config.hideAttributes) attributeHider = new AttributeHider(this);

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {

                started = true;
            }
        }, TimeUtil.secondsToTicks(config.startDelay));
    }

    @Override
    public void disable() {

        for (Set<PlayerPlacedBlock> set : playerPlacedBlocks.values()) {
            getDatabase().save(set);
        }
        RaidCraft.unregisterComponent(CustomItemManager.class);
    }

    public static class LocalConfiguration extends ConfigurationBase<RaidCraftPlugin> {

        public LocalConfiguration(RaidCraftPlugin plugin) {

            super(plugin, "config.yml");
        }

        @Setting("player-placed-block-worlds")
        public List<String> player_placed_block_worlds = new ArrayList<>();
        @Setting("server-start-delay")
        public double startDelay = 10.0;
        @Setting("hide-attributes")
        public boolean hideAttributes = true;
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> classes = new ArrayList<>();
        classes.add(PlayerPlacedBlock.class);
        classes.add(TObjectStorage.class);
        classes.add(TPersistentInventory.class);
        classes.add(TPersistentInventorySlot.class);
        return classes;
    }

    public void setPlayerPlaced(Block block) {

        if (!config.player_placed_block_worlds.contains(block.getWorld().getName())) {
            return;
        }
        PlayerPlacedBlock playerPlacedBlock = new PlayerPlacedBlock(block);
        if (!playerPlacedBlocks.containsKey(block.getChunk())) {
            playerPlacedBlocks.put(block.getChunk(), new HashSet<PlayerPlacedBlock>());
        }
        playerPlacedBlocks.get(block.getChunk()).add(playerPlacedBlock);
    }

    public boolean isPlayerPlaced(Block block) {

        if (!config.player_placed_block_worlds.contains(block.getWorld().getName())) {
            return false;
        }
        PlayerPlacedBlock playerPlacedBlock = new PlayerPlacedBlock(block);
        return playerPlacedBlocks.containsKey(block.getChunk()) && playerPlacedBlocks.get(block.getChunk()).contains(playerPlacedBlock);
    }

    public void removePlayerPlaced(Block block) {

        if (!config.player_placed_block_worlds.contains(block.getWorld().getName())) {
            return;
        }
        if (!playerPlacedBlocks.containsKey(block.getChunk())) {
            return;
        }
        PlayerPlacedBlock playerPlacedBlock = new PlayerPlacedBlock(block);
        if (playerPlacedBlocks.get(block.getChunk()).remove(playerPlacedBlock)) {
            PlayerPlacedBlock unique = getDatabase().find(PlayerPlacedBlock.class).where()
                    .eq("x", playerPlacedBlock.getX())
                    .eq("y", playerPlacedBlock.getY())
                    .eq("z", playerPlacedBlock.getZ())
                    .eq("world", playerPlacedBlock.getWorld()).findUnique();
            if (unique != null) {
                getDatabase().delete(unique);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onChunkLoad(ChunkLoadEvent event) {

        if (!config.player_placed_block_worlds.contains(event.getChunk().getWorld().getName())) {
            return;
        }
        Chunk chunk = event.getChunk();
        Set<PlayerPlacedBlock> set = getDatabase().find(PlayerPlacedBlock.class).where()
                .eq("chunk_x", chunk.getX())
                .eq("chunk_z", chunk.getZ()).findSet();
        if (set == null) {
            set = new HashSet<>();
        }
        playerPlacedBlocks.put(chunk, set);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onChunkUnload(ChunkUnloadEvent event) {

        if (!config.player_placed_block_worlds.contains(event.getChunk().getWorld().getName())) {
            return;
        }
        Set<PlayerPlacedBlock> remove = playerPlacedBlocks.remove(event.getChunk());
        if (remove == null || remove.isEmpty()) {
            return;
        }
        getDatabase().save(remove);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPreLogin(PlayerLoginEvent event) {

        if (!started) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "Server wird gerade gestartet...");
        }
    }
}

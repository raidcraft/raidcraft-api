package de.raidcraft;

import com.avaje.ebean.SqlUpdate;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.action.ActionCommand;
import de.raidcraft.api.action.action.ActionFactory;
import de.raidcraft.api.action.requirement.RequirementFactory;
import de.raidcraft.api.action.requirement.tables.TPersistantRequirement;
import de.raidcraft.api.action.requirement.tables.TPersistantRequirementMapping;
import de.raidcraft.api.action.trigger.TriggerManager;
import de.raidcraft.api.commands.ConfirmCommand;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.MultiComment;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.events.PlayerSignInteractEvent;
import de.raidcraft.api.inventory.InventoryManager;
import de.raidcraft.api.inventory.TPersistentInventory;
import de.raidcraft.api.inventory.TPersistentInventorySlot;
import de.raidcraft.api.items.CustomItemManager;
import de.raidcraft.api.items.attachments.ItemAttachmentManager;
import de.raidcraft.api.npc.NPC_Manager;
import de.raidcraft.api.player.PlayerStatisticProvider;
import de.raidcraft.api.random.GenericRDSTable;
import de.raidcraft.api.random.RDS;
import de.raidcraft.api.random.RDSNullValue;
import de.raidcraft.api.random.objects.ItemLootObject;
import de.raidcraft.api.random.objects.MoneyLootObject;
import de.raidcraft.api.random.objects.RandomMoneyLootObject;
import de.raidcraft.api.random.tables.ConfiguredRDSTable;
import de.raidcraft.api.storage.TObjectStorage;
import de.raidcraft.tables.PlayerPlacedBlock;
import de.raidcraft.tables.TActionApi;
import de.raidcraft.tables.TCommand;
import de.raidcraft.tables.TListener;
import de.raidcraft.tables.TLog;
import de.raidcraft.tables.TPlayerLog;
import de.raidcraft.tables.TPlayerLogStatistic;
import de.raidcraft.tables.TPlugin_;
import de.raidcraft.tables.TRcPlayer;
import de.raidcraft.util.TimeUtil;
import de.raidcraft.util.bossbar.BarAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import javax.persistence.PersistenceException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * @author Silthus
 */
public class RaidCraftPlugin extends BasePlugin implements Component, Listener {

    @Getter
    private LocalConfiguration config;
    private final Map<Chunk, Set<PlayerPlacedBlock>> playerPlacedBlocks = new HashMap<>();
    private final Map<UUID, Integer> playerLogs = new HashMap<>();
    private AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public void enable() {

        setupDatabase();
        this.config = configure(new LocalConfiguration(this));
        registerEvents(this);
        registerEvents(new RaidCraft());
        registerEvents(new BarAPI(this));
        registeerChildListener();
        registerCommands(ConfirmCommand.class, getName());
        registerCommands(ActionCommand.class, getName());
        RaidCraft.registerComponent(CustomItemManager.class, new CustomItemManager());
        RaidCraft.registerComponent(ItemAttachmentManager.class, new ItemAttachmentManager());
        RaidCraft.registerComponent(InventoryManager.class, new InventoryManager(this));
        // inizialize action API
        RequirementFactory.getInstance();
        ActionFactory.getInstance();
        TriggerManager.getInstance();

        // register random objects
        RDS.registerObject(new ItemLootObject.ItemLootFactory());
        RDS.registerObject(new MoneyLootObject.MoneyLootFactory());
        RDS.registerObject(new RandomMoneyLootObject.RandomMoneyLootFactory());
        RDS.registerObject(new RDSNullValue.RDSNullFactory());
        RDS.registerObject(new GenericRDSTable.GenericTableFactory());
        RDS.registerObject(new ConfiguredRDSTable.TableFactory());

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        if (config.preLoginKicker) {
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {

                    started.set(true);
                    RaidCraft.LOGGER.info("Player login now allowed");
                }
            }, TimeUtil.secondsToTicks(config.startDelay));
        } else {
            started.set(true);
        }

        // sync all ActionAPI stuff into Database
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {

                RaidCraft.trackActionApi();
            }
        }, TimeUtil.secondsToTicks(config.actoionapiSyncDelay));
        if (config.heartbeatTicks > 0) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Heartbeat(getLogger(), config.heartbeatTicks),
                    -1, config.heartbeatTicks);
        }
        // lets run this last if any mc errors occur
        // TODO: reimplement and find fix
        // if (config.hideAttributes) attributeHider = new AttributeHider(this);
    }

    private void setupDatabase() {

        try {
            // delete all commands
            SqlUpdate deleteCommands = getDatabase().createSqlUpdate("DELETE FROM rc_commands WHERE server = :server");
            deleteCommands.setParameter("server", Bukkit.getServerName());
            deleteCommands.execute();
        } catch (PersistenceException e) {
            e.printStackTrace();
            getLogger().warning("Installing database for " + getDescription().getName() + " due to first time usage");
            installDDL();
        }
    }

    @Override
    public void disable() {

        for (Set<PlayerPlacedBlock> set : playerPlacedBlocks.values()) {
            getDatabase().save(set);
        }
        for (UUID uuid : playerLogs.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                completePlayerLog(player);
            }
        }
        RaidCraft.unregisterComponent(CustomItemManager.class);
        // save all NPC's
        NPC_Manager.getInstance().storeAll();
    }

    public void registeerChildListener() {

        Bukkit.getPluginManager().registerEvents(new Listener() {
            public void onPlayerInteract(PlayerInteractEvent event) {

                if (event.getClickedBlock() == null || !(event.getClickedBlock() instanceof Sign)) {
                    return;
                }
                RaidCraft.callEvent(new PlayerSignInteractEvent(event));
            }
        }, this);
    }

    public static class LocalConfiguration extends ConfigurationBase<RaidCraftPlugin> {

        public LocalConfiguration(RaidCraftPlugin plugin) {

            super(plugin, "config.yml");
        }

        @Setting("player-placed-block-worlds")
        public List<String> player_placed_block_worlds = new ArrayList<>();
        @Setting("server-start-delay")
        public double startDelay = 10.0;
        @Setting("pre-login-kicker")
        public boolean preLoginKicker = true;
        @Setting("actionapi-to-db-delay")
        public int actoionapiSyncDelay = 10;
        @Setting("hide-attributes")
        public boolean hideAttributes = true;
        @Setting("action-api.parallel")
        public boolean parallelActionAPI = true;
        @Setting("heartbeat-ticks")
        @MultiComment({"prints a heartbeat message in this interval",
                "-1: off, 20: each second"})
        public long heartbeatTicks = -1;
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> classes = new ArrayList<>();
        classes.add(PlayerPlacedBlock.class);
        classes.add(TObjectStorage.class);
        classes.add(TPersistentInventory.class);
        classes.add(TPersistentInventorySlot.class);
        classes.add(TPersistantRequirement.class);
        classes.add(TPersistantRequirementMapping.class);
        classes.add(TCommand.class);
        classes.add(TActionApi.class);
        classes.add(TRcPlayer.class);
        classes.add(TListener.class);
        classes.add(TLog.class);
        classes.add(TPlugin_.class);
        classes.add(TPlayerLog.class);
        classes.add(TPlayerLogStatistic.class);
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {

        createPlayerLog(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {

        completePlayerLog(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {

        completePlayerLog(event.getPlayer());
        createPlayerLog(event.getPlayer());
    }

    private void createPlayerLog(Player player) {

        TPlayerLog log = new TPlayerLog();
        Timestamp joinTime = Timestamp.from(Instant.now());
        log.setJoinTime(joinTime);
        log.setPlayer(player.getUniqueId());
        log.setName(player.getName());
        log.setWorld(player.getLocation().getWorld().getName());
        getDatabase().save(log);
        for (Statistic statistic : Statistic.values()) {
            if (!statistic.isSubstatistic()) {
                TPlayerLogStatistic stat = new TPlayerLogStatistic();
                stat.setLog(log);
                stat.setStatistic(statistic.name());
                stat.setLogonValue(player.getStatistic(statistic));
                getDatabase().save(stat);
            }
        }
        for (Map.Entry<String, PlayerStatisticProvider> playerStat : RaidCraft.getStatisticProviders().entrySet()) {
            TPlayerLogStatistic stat = new TPlayerLogStatistic();
            stat.setLog(log);
            stat.setStatistic(playerStat.getKey());
            stat.setLogonValue(playerStat.getValue().getStatisticValue(player));
            getDatabase().save(stat);
        }
        TPlayerLog addedLog = getDatabase().find(TPlayerLog.class).where().eq("player", player.getUniqueId()).eq("join_time", joinTime).findUnique();
        if (addedLog == null) {
            getLogger().warning("Could not find added log for " + player.getName());
        } else {
            playerLogs.put(player.getUniqueId(), addedLog.getId());
        }
    }

    private void completePlayerLog(Player player) {

        int id = playerLogs.remove(player.getUniqueId());
        TPlayerLog log = getDatabase().find(TPlayerLog.class, id);
        if (log == null) {
            getLogger().warning("Could not find player log with id " + id);
            return;
        }
        log.setQuitTime(Timestamp.from(Instant.now()));
        getDatabase().update(log);
        for (TPlayerLogStatistic statistic : log.getStatistics()) {
            try {
                Statistic stat = Statistic.valueOf(statistic.getStatistic());
                if (stat != null) {
                    statistic.setLogoffValue(player.getStatistic(stat));
                } else {
                    PlayerStatisticProvider provider = RaidCraft.getStatisticProvider(statistic.getStatistic());
                    if (provider != null) {
                        statistic.setLogoffValue(provider.getStatisticValue(player));
                    }
                }
                getDatabase().update(statistic);
            } catch (IllegalArgumentException ignored) {
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
    public void preJoin(AsyncPlayerPreLoginEvent event) {

        if (!started.get()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                    "The server has just been started and is in the initialization phase ...");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void preJoinUUID(AsyncPlayerPreLoginEvent event) {

        UUID uuid = event.getUniqueId();
        String name = event.getName();

        TRcPlayer player = getDatabase().find(TRcPlayer.class)
                .where().eq("uuid", uuid.toString()).findUnique();
        // known player
        if (player != null) {
            // if name changed
            if (!player.getLastName().equalsIgnoreCase(name)) {
                getLogger().warning("---- NAME CHANGE FOUND (" + uuid + ") !!! ----");
                getLogger().warning("---- old name (" + player.getLastName() + ") !!! ----");
                getLogger().warning("---- new name (" + name + ") !!! ----");
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        "You changed your playername. Contact raid-craft.de to reactivate.");
            }
            player.setLastJoined(new Date());
            getDatabase().save(player);
            return;
        }
        // new player
        player = getDatabase().find(TRcPlayer.class)
                .where().ieq("last_name", name).findUnique();
        // check if name already in use
        if (player != null) {
            getLogger().warning("---- NEW UUID FOR NAME (" + name + ") FOUND !!! ----");
            getLogger().warning("---- new uuid (" + uuid + ") FOUND !!! ----");
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    "Your playername is protected. Visit raid-craft.de for more informations");
            return;
        }
        // add new player
        player = new TRcPlayer();
        player.setLastName(name);
        player.setUuid(uuid);
        Date currentTime = new Date();
        player.setFirstJoined(currentTime);
        player.setLastJoined(currentTime);
        getDatabase().save(player);
    }


    /**
     * Do not call this method
     * use registerCommands(Class<?> class, String host)
     * @param clazz
     */
    public void trackCommand(Class<?> clazz, String host, String baseClass) {

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            Command anno_cmd = method.getAnnotation(Command.class);
            if (anno_cmd == null) {
                continue;
            }
            NestedCommand anno_nested = method.getAnnotation(NestedCommand.class);
            if (anno_nested != null) {
                for (Class<?> childClass : anno_nested.value()) {
                    trackCommand(childClass, host, TCommand.printArray(anno_cmd.aliases()));
                }
                continue;
            }
            getDatabase().save(TCommand.parseCommand(method, host, baseClass));
        }
    }

    public class Heartbeat implements Runnable {

        private long start;
        private long tick;
        private long interval;
        private Logger logger;

        public Heartbeat(Logger logger, long interval) {

            this.logger = logger;
            start = System.currentTimeMillis();
        }

        @Override
        public void run() {

            tick++;
            logger.info("Heartbeat: " + tick + " (diff: " + getDiff() + ")");
        }

        public long getDiff() {

            return System.currentTimeMillis() - (start + (tick * interval) * (1000 / 20));
        }
    }
}

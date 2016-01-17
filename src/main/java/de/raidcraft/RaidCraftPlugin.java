package de.raidcraft;

import com.avaje.ebean.SqlUpdate;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.NestedCommand;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.ActionCommand;
import de.raidcraft.api.action.GlobalAction;
import de.raidcraft.api.action.GlobalRequirement;
import de.raidcraft.api.action.requirement.global.IfElseRequirement;
import de.raidcraft.api.action.requirement.tables.TPersistantRequirement;
import de.raidcraft.api.action.requirement.tables.TPersistantRequirementMapping;
import de.raidcraft.api.action.trigger.global.GlobalPlayerTrigger;
import de.raidcraft.api.action.trigger.global.TimerTrigger;
import de.raidcraft.api.commands.ConfirmCommand;
import de.raidcraft.api.config.Comment;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.MultiComment;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.events.PlayerSignInteractEvent;
import de.raidcraft.api.inventory.InventoryManager;
import de.raidcraft.api.inventory.TPersistentInventory;
import de.raidcraft.api.inventory.TPersistentInventorySlot;
import de.raidcraft.api.inventory.sync.InventorySync;
import de.raidcraft.api.inventory.sync.TPlayerInventory;
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
import de.raidcraft.tables.*;
import de.raidcraft.util.BlockUtil;
import de.raidcraft.util.TimeUtil;
import de.raidcraft.util.bossbar.BarAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;

import javax.persistence.PersistenceException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public class RaidCraftPlugin extends BasePlugin implements Component, Listener {

    @Getter
    private LocalConfiguration config;
    private final Map<UUID, Integer> playerLogs = new HashMap<>();
    private AtomicBoolean started = new AtomicBoolean(false);
    private WorldGuardPlugin worldGuardPlugin;

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

        if(getConfig().enablePlayerInventorySave) {
            registerEvents(new InventorySync(this));
        }

        // inizialize action API
        registerActionAPI();

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

                    ActionAPI.UNKNOWN_ACTIONS.stream()
                            .filter(action -> !ActionAPI.isAction(action))
                            .forEach(action -> RaidCraft.LOGGER.warning("unknown action: " + action));
                    ActionAPI.UNKNOWN_REQUIREMENTS.stream()
                            .filter(requirement -> !ActionAPI.isRequirement(requirement))
                            .forEach(requirement -> RaidCraft.LOGGER.warning("unknown requirement: " + requirement));
                    ActionAPI.UNKNOWN_TRIGGER.stream()
                            .filter(trigger -> !ActionAPI.isRequirement(trigger))
                            .forEach(trigger -> RaidCraft.LOGGER.warning("unknown trigger: " + trigger));
                    ActionAPI.UNKNOWN_ACTIONS.clear();
                    ActionAPI.UNKNOWN_REQUIREMENTS.clear();
                    ActionAPI.UNKNOWN_TRIGGER.clear();
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
    }

    private void registerActionAPI() {

        ActionAPI actionAPI = ActionAPI.register(this).global();
        for (GlobalAction action : GlobalAction.values()) {
            actionAPI.action(action.getAction());
        }
        for (GlobalRequirement requirement : GlobalRequirement.values()) {
            actionAPI.requirement(requirement.getRequirement());
        }
        if (getConfig().actionApiGlobalPlayerTrigger) {
            actionAPI.trigger(new GlobalPlayerTrigger());
        }
        if (getConfig().actionApiTimerTrigger) {
            actionAPI.trigger(new TimerTrigger());
        }
        actionAPI.requirement(new IfElseRequirement<>(), Object.class);
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

//        for (UUID uuid : new ArrayList<>(playerLogs.keySet())) {
//            Player player = Bukkit.getPlayer(uuid);
//            if (player != null) {
//                completePlayerLog(player);
//            }
//        }
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

    public Optional<WorldGuardPlugin> getWorldGuard() {

        if (worldGuardPlugin == null) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
            if (plugin != null) worldGuardPlugin = (WorldGuardPlugin) plugin;
        }
        return Optional.ofNullable(worldGuardPlugin);
    }

    public static class LocalConfiguration extends ConfigurationBase<RaidCraftPlugin> {

        public LocalConfiguration(RaidCraftPlugin plugin) {

            super(plugin, "config.yml");
        }

        @Setting("check-player-block-placement")
        public boolean checkPlayerBlockPlacement = false;
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
        @Setting("debug.actions")
        public boolean debugActions = true;
        @Setting("debug.trigger")
        public boolean debugTrigger = true;
        @Setting("debug.requirements")
        public boolean debugRequirements = true;

        @Comment("Enable GlobalPlayerTrigger, like interact, block.break, block.place, move, craft, death, join")
        @Setting("action-api.enable-global-player-trigger")
        private boolean actionApiGlobalPlayerTrigger = true;
        @Comment("Enable TimerTrigger, like tick, end, cancel")
        @Setting("action-api.enable-time-trigger")
        private boolean actionApiTimerTrigger = true;
        @Comment("Save and Load Player Inventories in Database, allow sharing with other servers")
        @Setting("enable-player-inventory-share")
        private boolean enablePlayerInventorySave = false;
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
        classes.add(TPlayerInventory.class);
        return classes;
    }

//    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
//    public void onPlayerJoin(PlayerJoinEvent event) {
//
//        createPlayerLog(event.getPlayer());
//    }
//
//    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
//    public void onPlayerQuit(PlayerQuitEvent event) {
//
//        completePlayerLog(event.getPlayer());
//    }
//
//    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
//    public void onPlayerKick(PlayerKickEvent event) {
//
//        completePlayerLog(event.getPlayer());
//    }
//
//    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
//    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
//
//        completePlayerLog(event.getPlayer());
//        createPlayerLog(event.getPlayer());
//    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerPlacedBlock(BlockPlaceEvent event) {

        if (getConfig().checkPlayerBlockPlacement) {
            BlockUtil.addPlayerPlacedBlock(event.getBlock());
        }
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

        if (player == null || player.getUniqueId() == null || !playerLogs.containsKey(player.getUniqueId())) return;
        int id = playerLogs.remove(player.getUniqueId());
        TPlayerLog log = getDatabase().find(TPlayerLog.class, id);
        if (log == null) {
            getLogger().warning("Could not find player log with id " + id);
            return;
        }
        log.setQuitTime(Timestamp.from(Instant.now()));
        getDatabase().update(log);
        Set<String> stats = Arrays.asList(Statistic.values()).stream().map(s -> s.name()).collect(Collectors.toSet());
        List<TPlayerLogStatistic> statistics = log.getStatistics();
        for (TPlayerLogStatistic statistic : statistics) {
            if (stats.contains(statistic.getStatistic())) {
                statistic.setLogoffValue(player.getStatistic(Statistic.valueOf(statistic.getStatistic())));
            } else {
                PlayerStatisticProvider provider = RaidCraft.getStatisticProvider(statistic.getStatistic());
                if (provider != null) {
                    statistic.setLogoffValue(provider.getStatisticValue(player));
                }
            }
            getDatabase().update(statistic);
        }
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
     *
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

package de.raidcraft;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.NestedCommand;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.ActionCommand;
import de.raidcraft.api.action.GlobalAction;
import de.raidcraft.api.action.GlobalRequirement;
import de.raidcraft.api.action.action.GroupAction;
import de.raidcraft.api.action.action.global.DynamicPlayerTextAction;
import de.raidcraft.api.action.action.global.DynamicPlayerTextManager;
import de.raidcraft.api.action.flow.FlowType;
import de.raidcraft.api.action.requirement.GroupRequirement;
import de.raidcraft.api.action.requirement.global.IfElseRequirement;
import de.raidcraft.api.action.requirement.tables.TPersistantRequirement;
import de.raidcraft.api.action.requirement.tables.TPersistantRequirementMapping;
import de.raidcraft.api.tags.TPlayerTag;
import de.raidcraft.api.tags.TTag;
import de.raidcraft.api.action.trigger.TriggerGroup;
import de.raidcraft.api.action.trigger.global.GlobalPlayerTrigger;
import de.raidcraft.api.action.trigger.global.TimerTrigger;
import de.raidcraft.api.commands.ConfirmCommand;
import de.raidcraft.api.config.*;
import de.raidcraft.api.config.builder.ConfigGenerator;
import de.raidcraft.api.disguise.Disguise;
import de.raidcraft.api.disguise.DisguiseCommand;
import de.raidcraft.api.disguise.DisguiseManager;
import de.raidcraft.api.events.PlayerSignInteractEvent;
import de.raidcraft.api.inventory.InventoryManager;
import de.raidcraft.api.inventory.TPersistentInventory;
import de.raidcraft.api.inventory.TPersistentInventorySlot;
import de.raidcraft.api.inventory.sync.InventorySync;
import de.raidcraft.api.inventory.sync.TPlayerInventory;
import de.raidcraft.api.items.CustomItemManager;
import de.raidcraft.api.items.attachments.ItemAttachmentManager;
import de.raidcraft.api.npc.NPC_Manager;
import de.raidcraft.api.player.PlayerResolver;
import de.raidcraft.api.player.PlayerStatisticProvider;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.api.random.GenericRDSTable;
import de.raidcraft.api.random.RDS;
import de.raidcraft.api.random.RDSNullValue;
import de.raidcraft.api.random.objects.ItemLootObject;
import de.raidcraft.api.random.objects.MoneyLootObject;
import de.raidcraft.api.random.objects.RandomMoneyLootObject;
import de.raidcraft.api.random.tables.ConfiguredRDSTable;
import de.raidcraft.api.storage.TObjectStorage;
import de.raidcraft.api.tags.TagCommands;
import de.raidcraft.tables.*;
import de.raidcraft.tracking.BlockTracking;
import de.raidcraft.util.BlockTracker;
import de.raidcraft.util.BukkitPlayerResolver;
import de.raidcraft.util.ConfigUtil;
import de.raidcraft.util.TimeUtil;
import de.raidcraft.util.bossbar.BarAPI;
import io.ebean.EbeanServer;
import io.ebean.SqlUpdate;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
    private AtomicBoolean started = new AtomicBoolean(false);
    private WorldGuardPlugin worldGuardPlugin;
    private BlockTracker blockTracker;
    private PlayerResolver playerResolver;
    private DisguiseManager disguiseManager;
    private final Map<UUID, Integer> playerLogs = new HashMap<>();

    @Override
    public void enable() {

        setupDatabase();
        this.config = configure(new LocalConfiguration(this));
        this.blockTracker = new BlockTracking(this);
        this.playerResolver = new BukkitPlayerResolver(this);
        this.disguiseManager = new DisguiseManager(this);
        registerEvents(this);
        registerEvents(new RaidCraft());
        registerEvents(new BarAPI(this));

        registeerChildListener();
        registerCommands(ConfirmCommand.class, getName());
        registerCommands(ActionCommand.class, getName());
        registerCommands(DisguiseCommand.class);
        registerCommands(DynamicPlayerTextManager.class);
        registerCommands(TagCommands.class);
        RaidCraft.registerComponent(CustomItemManager.class, new CustomItemManager());
        RaidCraft.registerComponent(ItemAttachmentManager.class, new ItemAttachmentManager());
        RaidCraft.registerComponent(InventoryManager.class, new InventoryManager(this));
        RaidCraft.registerComponent(DynamicPlayerTextManager.class, new DynamicPlayerTextManager(this));

        if(getConfig().enablePlayerInventorySave) {
            registerEvents(new InventorySync(this));
        }

        // inizialize Action API
        registerActionAPI();

        // register random objects
        RDS.registerObject(new ItemLootObject.ItemLootFactory());
        RDS.registerObject(new MoneyLootObject.MoneyLootFactory());
        RDS.registerObject(new RandomMoneyLootObject.RandomMoneyLootFactory());
        RDS.registerObject(new RDSNullValue.RDSNullFactory());
        RDS.registerObject(new GenericRDSTable.GenericTableFactory());
        RDS.registerObject(new ConfiguredRDSTable.TableFactory());

        Quests.registerQuestLoader(new ConfigLoader<RaidCraftPlugin>(this, "trigger", -5) {
            @Override
            public void loadConfig(String id, ConfigurationBase<RaidCraftPlugin> config) {
                ActionAPI.register(RaidCraftPlugin.this).global()
                        .trigger(new TriggerGroup(id, config));
            }
        });

        Bukkit.getScheduler().runTaskLater(this, () -> ConfigUtil.loadRecursiveConfigs(this, "trigger", new ConfigLoader<RaidCraftPlugin>(this) {
            @Override
            public void loadConfig(String id, ConfigurationBase<RaidCraftPlugin> config) {
                ActionAPI.register(RaidCraftPlugin.this).global()
                        .trigger(new TriggerGroup(id, config));
            }
        }), 1L);

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

        if (config.heartbeatTicks > 0) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Heartbeat(getLogger(), config.heartbeatTicks),
                    -1, config.heartbeatTicks);
        }

        // sync all ActionAPI stuff into Database
        Bukkit.getScheduler().runTaskLater(this, this::trackActionApi, TimeUtil.secondsToTicks(getConfig().actoionapiSyncDelay));

        Bukkit.getScheduler().runTaskLater(this, this::registerItems, 1L);
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
        actionAPI.action(new GroupAction<>(), Player.class);
        actionAPI.action(new DynamicPlayerTextAction(), Player.class);
        actionAPI.requirement(new GroupRequirement<>(), Object.class);
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> classes = new ArrayList<>();
        classes.add(TObjectStorage.class);
        classes.add(TPersistentInventory.class);
        classes.add(TPersistentInventorySlot.class);
        classes.add(TPersistantRequirement.class);
        classes.add(TPersistantRequirementMapping.class);
        classes.add(TPlayerInventory.class);
        classes.add(PlayerPlacedBlock.class);
        classes.add(TCommand.class);
        classes.add(TActionApi.class);
        classes.add(TRcPlayer.class);
        classes.add(TListener.class);
        classes.add(TPlugin.class);
        classes.add(TPlayerLog.class);
        classes.add(TPlayerLogStatistic.class);
        classes.add(TPlayerTag.class);
        classes.add(TTag.class);
        classes.add(Disguise.class);
        classes.add(TMinecraftItem.class);
        return classes;
    }

    private void registerItems() {
        getLogger().info("Synchronizing Minecraft items into database...");
        int i = 0;
        for (Material material : Material.values()) {
            if (material.isLegacy()) continue;
            if (TMinecraftItem.createOrUpdate(material)) i++;
        }
        getLogger().info("... created or updated " + i + " Minecraft items.");
    }

    private void setupDatabase() {

        try {
            // delete all commands
            SqlUpdate deleteCommands = getRcDatabase().createSqlUpdate("DELETE FROM rc_commands WHERE server = :server");
            deleteCommands.setParameter("server", Bukkit.getServerName());
            deleteCommands.execute();
        } catch (PersistenceException e) {
            e.printStackTrace();
            getLogger().warning("Installing database for " + getDescription().getName() + " due to first time usage");
        }
    }

    @Override
    public void disable() {

        RaidCraft.unregisterComponent(CustomItemManager.class);
        // save all NPC's
        NPC_Manager.getInstance().storeAll();

        for (UUID uuid : new ArrayList<>(playerLogs.keySet())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                completePlayerLog(player);
            }
        }
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


        @Setting("server-start-delay")
        public double startDelay = 10.0;
        @Setting("pre-login-kicker")
        public boolean preLoginKicker = true;
        @Setting("hide-attributes")
        public boolean hideAttributes = true;
        @Setting("action-api.parallel")
        public boolean parallelActionAPI = true;
        @Setting("heartbeat-ticks")
        @MultiComment({"prints a heartbeat message in this interval",
                "-1: off, 20: each second"})
        public long heartbeatTicks = -1;
        @Setting("debug.flow")
        public boolean debugFlowParser = false;
        @Setting("debug.actions")
        public boolean debugActions = true;
        @Setting("debug.trigger")
        public boolean debugTrigger = true;
        @Setting("debug.requirements")
        public boolean debugRequirements = true;
        @Setting("debug.excluded-trigger")
        public List<String> excludedTrigger = new ArrayList<>();

        @Setting("pastebin.apikey")
        @Comment("Get your pastebin api key from: https://pastebin.com/api")
        public String pastebinApiKey = "";

        @Comment("Enable GlobalPlayerTrigger, like interact, block.break, block.place, move, craft, death, join")
        @Setting("action-api.enable-global-player-trigger")
        private boolean actionApiGlobalPlayerTrigger = true;
        @Comment("Enable TimerTrigger, like tick, end, cancel")
        @Setting("action-api.enable-time-trigger")
        private boolean actionApiTimerTrigger = true;
        @Comment("Save and Load Player Inventories in Database, allow sharing with other servers")
        @Setting("enable-player-inventory-share")
        private boolean enablePlayerInventorySave = false;
        @Comment("The average words per minute a player can read.")
        @Setting("dynamic-actions.average-words-per-minute")
        public int averageWordsPerMinute = 150;
        @Comment("The minimum delay gets added to the average word delay.")
        @Setting("dynamic-actions.min-delay")
        public String minDynamicTextActionDelay = "2s";

        @Setting("check-player-block-placement")
        public boolean checkPlayerBlockPlacement = false;
        @Setting("player-placed-block-worlds")
        public List<String> player_placed_block_worlds = new ArrayList<>();
        @Setting("actionapi-to-db-delay")
        public int actoionapiSyncDelay = 10;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void preJoin(AsyncPlayerPreLoginEvent event) {

        if (!started.get()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                    "The server has just been started and is in the initialization phase ...");
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {

        createPlayerLog(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {

        completePlayerLog(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerKick(PlayerKickEvent event) {

        completePlayerLog(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
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
        getRcDatabase().save(log);
        for (Statistic statistic : Statistic.values()) {
            if (!statistic.isSubstatistic()) {
                TPlayerLogStatistic stat = new TPlayerLogStatistic();
                stat.setLog(log);
                stat.setStatistic(statistic.name());
                stat.setLogonValue(player.getStatistic(statistic));
                getRcDatabase().save(stat);
            }
        }
        for (Map.Entry<String, PlayerStatisticProvider> playerStat : RaidCraft.getStatisticProviders().entrySet()) {
            TPlayerLogStatistic stat = new TPlayerLogStatistic();
            stat.setLog(log);
            stat.setStatistic(playerStat.getKey());
            stat.setLogonValue(playerStat.getValue().getStatisticValue(player));
            getRcDatabase().save(stat);
        }
        TPlayerLog addedLog = getRcDatabase().find(TPlayerLog.class).where().eq("player", player.getUniqueId()).eq("join_time", joinTime).findOne();
        if (addedLog == null) {
            getLogger().warning("Could not find added log for " + player.getName());
        } else {
            playerLogs.put(player.getUniqueId(), addedLog.getId());
        }
    }

    private void completePlayerLog(Player player) {

        if (player == null || player.getUniqueId() == null || !playerLogs.containsKey(player.getUniqueId())) return;
        int id = playerLogs.remove(player.getUniqueId());
        TPlayerLog log = getRcDatabase().find(TPlayerLog.class, id);
        if (log == null) {
            getLogger().warning("Could not find player log with id " + id);
            return;
        }
        log.setQuitTime(Timestamp.from(Instant.now()));
        getRcDatabase().update(log);
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
            getRcDatabase().update(statistic);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void preJoinUUID(AsyncPlayerPreLoginEvent event) {

        UUID uuid = event.getUniqueId();
        String name = event.getName();

        TRcPlayer player = getRcDatabase().find(TRcPlayer.class)
                .where().eq("uuid", uuid.toString()).findOne();
        // known player
        if (player != null) {
            // if displayName changed
            if (!player.getLastName().equalsIgnoreCase(name)) {
                getLogger().warning("---- NAME CHANGE FOUND (" + uuid + ") !!! ----");
                getLogger().warning("---- old displayName (" + player.getLastName() + ") !!! ----");
                getLogger().warning("---- new displayName (" + name + ") !!! ----");
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        "You changed your playername. Contact raid-craft.de to reactivate.");
            }
            player.setLastJoined(new Date());
            getRcDatabase().save(player);
            return;
        }
        // new player
        player = getRcDatabase().find(TRcPlayer.class)
                .where().ieq("last_name", name).findOne();
        // check if displayName already in use
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
        getRcDatabase().save(player);
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
            getRcDatabase().save(TCommand.parseCommand(method, host, baseClass));
        }
    }

    public void trackActionApi() {
        getRcDatabase().find(TActionApi.class).delete();
        trackActionApi(FlowType.ACTION, ActionAPI.getActions());
        trackActionApi(FlowType.REQUIREMENT, ActionAPI.getRequirements());
        trackActionApi(FlowType.TRIGGER, ActionAPI.getTrigger());
    }

    public <T extends ConfigGenerator> void trackActionApi(FlowType type, Map<String, T> map) {

        EbeanServer db = RaidCraftPlugin.getPlugin(RaidCraftPlugin.class).getRcDatabase();
        String server = Bukkit.getServerName();
        for (String key : map.keySet()) {
            T entry = map.get(key);
            if (entry == null) {
                continue;
            }
            TActionApi actionApi = db.find(TActionApi.class)
                    .where()
                    .eq("name", key)
                    .eq("action_type", type.name().toLowerCase())
                    .findOne();
            if (actionApi == null) {
                actionApi = new TActionApi();
                actionApi.setName(key);
                actionApi.setAction_type(type.name().toLowerCase());
                actionApi.setServer(server);
                Optional<ConfigGenerator.Information> information = Optional.empty();
                switch (type) {
                    case ACTION:
                        information = ActionAPI.getActionInformation(key);
                        break;
                    case REQUIREMENT:
                        information = ActionAPI.getRequirementInformation(key);
                        break;
                    case TRIGGER:
                        information = ActionAPI.getTriggerInformation(key);
                        break;
                }
                if (information.isPresent()) {
                    actionApi.setDescription(information.get().desc());
                    actionApi.setConf(String.join(";", information.get().conf()));
                }
            }
            actionApi.setActive(true);
            actionApi.setLastActive(new Date());
            db.save(actionApi);
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

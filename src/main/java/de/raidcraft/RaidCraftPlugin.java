package de.raidcraft;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.ActionCommand;
import de.raidcraft.api.action.GlobalAction;
import de.raidcraft.api.action.GlobalRequirement;
import de.raidcraft.api.action.action.GroupAction;
import de.raidcraft.api.action.requirement.GroupRequirement;
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
import de.raidcraft.api.random.GenericRDSTable;
import de.raidcraft.api.random.RDS;
import de.raidcraft.api.random.RDSNullValue;
import de.raidcraft.api.random.objects.ItemLootObject;
import de.raidcraft.api.random.objects.MoneyLootObject;
import de.raidcraft.api.random.objects.RandomMoneyLootObject;
import de.raidcraft.api.random.tables.ConfiguredRDSTable;
import de.raidcraft.api.storage.TObjectStorage;
import de.raidcraft.util.TimeUtil;
import de.raidcraft.util.bossbar.BarAPI;
import io.ebean.SqlUpdate;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * @author Silthus
 */
public class RaidCraftPlugin extends BasePlugin implements Component, Listener {

    @Getter
    private LocalConfiguration config;
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

        // inizialize Action API
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
        actionAPI.action(new GroupAction<>(), Player.class);
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
        return classes;
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

        @Setting("server-startStage-delay")
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
        @Setting("debug.actions")
        public boolean debugActions = true;
        @Setting("debug.trigger")
        public boolean debugTrigger = true;
        @Setting("debug.requirements")
        public boolean debugRequirements = true;

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
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void preJoin(AsyncPlayerPreLoginEvent event) {

        if (!started.get()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                    "The server has just been started and is in the initialization phase ...");
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

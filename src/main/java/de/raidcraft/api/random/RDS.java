package de.raidcraft.api.random;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.random.tables.ConfiguredRDSTable;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.Map;
import java.util.Optional;

/**
 * @author mdoering
 */
public class RDS {

    private static final Map<String, RDSObjectFactory> registeredFactories = new CaseInsensitiveMap<>();
    private static final Map<String, Map<String, RDSTable>> registeredTables = new CaseInsensitiveMap<>();

    public static void registerObject(RDSObjectFactory creator) {

        Class<? extends RDSObjectFactory> creatorClass = creator.getClass();
        if (!creatorClass.isAnnotationPresent(RDSObjectFactory.Name.class)) {
            RaidCraft.LOGGER.warning("RDSObjectFactory has no name defined! " + creatorClass.getCanonicalName());
            return;
        }
        registeredFactories.put(creatorClass.getAnnotation(RDSObjectFactory.Name.class).value(), creator);
    }

    public static void unregisterObject(RDSObjectFactory factory) {

        Class<? extends RDSObjectFactory> creatorClass = factory.getClass();
        if (!creatorClass.isAnnotationPresent(RDSObjectFactory.Name.class)) {
            RaidCraft.LOGGER.warning("RDSObjectFactory has no name defined! " + creatorClass.getCanonicalName());
            return;
        }
        unregisterObject(creatorClass.getAnnotation(RDSObjectFactory.Name.class).value());
    }

    public static void unregisterObject(String name) {

        registeredFactories.remove(name);
    }

    public static Optional<RDSObject> createObject(String name, ConfigurationSection config) {

        Optional<RDSObjectFactory> creator = getObjectCreator(name);
        if (!creator.isPresent()) return Optional.empty();
        RDSObject object = creator.get()
                .createInstance(config.isConfigurationSection("args") ? config.getConfigurationSection("args") : new MemoryConfiguration());
        object.setEnabled(config.getBoolean("enabled", true));
        object.setAlways(config.getBoolean("always", false));
        object.setUnique(config.getBoolean("unique", false));
        object.setProbability(config.getDouble("probability", 1));
        if (object instanceof RDSTable) {
            ((RDSTable) object).setCount(config.getInt("count", 1));
        }
        return Optional.ofNullable(object);
    }

    public static Optional<RDSObjectFactory> getObjectCreator(String name) {

        return Optional.ofNullable(registeredFactories.get(name));
    }

    private static void registerTable(BasePlugin plugin, String name, RDSTable table) {

        if (!registeredTables.containsKey(plugin.getName())) {
            registeredTables.put(plugin.getName(), new CaseInsensitiveMap<>());
        }
        registeredTables.get(plugin.getName()).put(name, table);
        plugin.getLogger().info("Registered loot table (" + table.getClass().getTypeName() + "): " + name);
    }

    public static void registerTable(BasePlugin plugin, RDSTable table) {

        Class<? extends RDSTable> tableClass = table.getClass();
        if (!tableClass.isAnnotationPresent(RDSTable.Name.class)) {
            if (table instanceof ConfiguredRDSTable) {
                registerTable(plugin, ((ConfiguredRDSTable) table).getName(), table);
                return;
            }
            RaidCraft.LOGGER.warning("RDSTable has no name defined! " + tableClass.getCanonicalName());
            return;
        }
        registerTable(plugin, tableClass.getAnnotation(RDSTable.Name.class).value(), table);
    }

    public static void unregisterTable(BasePlugin plugin, String name) {

        registeredTables.getOrDefault(plugin, new CaseInsensitiveMap<>()).remove(name);
    }

    public static void unregisterTable(BasePlugin plugin, RDSTable table) {

        Class<? extends RDSTable> tableClass = table.getClass();
        if (!tableClass.isAnnotationPresent(RDSTable.Name.class)) {
            if (table instanceof ConfiguredRDSTable) {
                unregisterTable(plugin, ((ConfiguredRDSTable) table).getName());
                return;
            }
            RaidCraft.LOGGER.warning("RDSTable has no name defined! " + tableClass.getCanonicalName());
            return;
        }
        unregisterTable(plugin, tableClass.getAnnotation(RDSTable.Name.class).value());
    }

    public static void unregisterTables(BasePlugin plugin) {

        registeredTables.remove(plugin.getName());
    }

    public static Optional<RDSTable> getTable(String name) {

        for (Map<String, RDSTable> tableMap : registeredTables.values()) {
            if (tableMap.containsKey(name)) {
                return Optional.of(tableMap.get(name));
            }
        }
        return Optional.empty();
    }
}

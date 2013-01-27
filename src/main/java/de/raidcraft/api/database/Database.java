package de.raidcraft.api.database;

import com.avaje.ebean.Ebean;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * Based on sk89q WorldGuard!
 * https://github.com/sk89q/worldguard/blob/master/src/main/java/com/sk89q/worldguard/protection/databases/MySQLDatabase.java
 */
public class Database {

    private static Map<Class<?>, Table> tables = new HashMap<>();
    private DatabaseConfig config;
    private Connection connection;

    public Database(BasePlugin plugin) {

        this.config = plugin.configure(new DatabaseConfig(plugin));

        try {
            connect();
            plugin.getLogger().info("[Database] MySQL-Status: Database connection successful established!");
        } catch (SQLException ex) {
            plugin.getLogger().warning("[Database] MySQL-Error: " + ex.getMessage());
        }
    }

    public DatabaseConfig getConfig() {

        return config;
    }

    public static class DatabaseConfig extends ConfigurationBase<BasePlugin> {

        private static final String CONFIG_NAME = "database.yml";

        @Setting("database.hostname")
        public String hostname = "localhost:3306";
        @Setting("database.database")
        public String database = "minecraft";
        @Setting("database.username")
        public String username = "minecraft";
        @Setting("database.password")
        public String password = "password";

        public DatabaseConfig(BasePlugin plugin) {

            super(plugin, CONFIG_NAME);
        }
    }

    private void connect() throws SQLException {

        if (connection != null) {
            // Make a dummy query to check the connnection is alive.
            try {
                connection.prepareStatement("SELECT 1;").execute();
            } catch (SQLException ex) {
                if ("08S01".equals(ex.getSQLState())) {
                    connection.close();
                }
            }
        }
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:mysql://" + config.hostname + "/" + config.database, config.username, config.password);
            createTables();
        }
    }

    public void registerTable(Class<? extends Table> clazz, Table table) {

        tables.put(clazz, table);
        if (connection == null) {
            return;
        }
        table.setConnection(connection);
        if (getExistingTables(table.getTableName()).size() <= 0) {
            table.createTable();
        }
    }

    private void createTables() {

        Set<String> existingTables = getExistingTables("%");

        for (Map.Entry<Class<?>, Table> entry : tables.entrySet()) {
            if (!existingTables.contains(entry.getValue().getTableName())) {
                entry.getValue().createTable();
            }
        }
    }

    private Set<String> getExistingTables(String search) {

        Set<String> existingTables = new HashSet<>();

        try {
            ResultSet resultSet = connection.getMetaData().getTables(null, null, search, null);
            while (resultSet.next()) {
                String column = resultSet.getString(3);
                existingTables.add(column);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return existingTables;
    }

    public static <T extends Table> T getTable(Class<T> cls) {

        return cls.cast(tables.get(cls));
    }

    public static void save(Bean bean) {

        if (Ebean.find(bean.getClass(), bean.getId()) == null) {
            Ebean.save(bean);
        } else {
            Ebean.update(bean);
        }
    }
}

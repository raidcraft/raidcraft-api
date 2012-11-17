package de.raidcraft.api.database;

import com.sk89q.rebar.config.annotations.Setting;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;

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

    private static Database instance;

    public static Database getInstance() {

        return instance;
    }

    private LocalConfiguration config;
    private static Connection connection;
    private static Map<Class<?>, Table> tables = new HashMap<>();

    public Database(BasePlugin plugin) {

        if (instance != null) return;
        instance = this;
        this.config = new LocalConfiguration(plugin, "config.yml");

        try {
            connect();
            plugin.getLogger().info("[Database] MySQL-Status: Database connection successful established!");
        } catch (SQLException ex) {
            plugin.getLogger().warning("[Database] MySQL-Error: " + ex.getMessage());
        }
    }

    private static class LocalConfiguration extends ConfigurationBase {

        @Setting("database.host")
        public String host = "localhost:3306";
        @Setting("database.database")
        public String database = "minecraft";
        @Setting("database.username")
        public String sqlUsername = "minecraft";
        @Setting("database.password")
        public String sqlPassword = "password";

        public LocalConfiguration(BasePlugin plugin, String name) {

            super(plugin, name);
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
            connection = DriverManager.getConnection("jdbc:mysql://" + config.host + "/" + config.database, config.sqlUsername, config.sqlPassword);
            createTables();
        }
    }

    public void registerTable(Class<? extends Table> clazz, Table table) {

        tables.put(clazz, table);
        if (instance == null || connection == null) {
            return;
        }
        if (!(instance.getExistingTables(table.getTableName()).size() > 0)) {
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
            ResultSet resultSet = getConnection().getMetaData().getTables(null, null, search, null);
            while (resultSet.next()) {
                String column = resultSet.getString(3);
                existingTables.add(column);
            }
        } catch (SQLException e) {
        }

        return existingTables;
    }

    public static Connection getConnection() {

        return connection;
    }

    public static <T extends Table> T getTable(Class<T> cls) {

        return cls.cast(tables.get(cls));
    }
}

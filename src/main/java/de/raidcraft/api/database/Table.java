package de.raidcraft.api.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Author: Philip
 * Date: 16.09.12 - 15:03
 * Description:
 */
public abstract class Table {

    private Connection connection;
    private String prefix = "modules_";
    private String tableName;

    public Table(String tableName) {

        this.tableName = tableName;
    }

    public Table(String tableName, String ownPrefix) {

        this.tableName = tableName;
        this.prefix = ownPrefix;
    }

    public abstract void createTable();

    public final String getTableName() {

        return prefix + tableName;
    }

    public final Connection getConnection() {

        return connection;
    }

    public final void setConnection(Connection connection) {

        this.connection = connection;
    }

    public final ResultSet executeQuery(String sql) throws SQLException {

        return getConnection().createStatement().executeQuery(sql);
    }

    public final int executeUpdate(String sql) throws SQLException {

        return getConnection().createStatement().executeUpdate(sql);
    }
}

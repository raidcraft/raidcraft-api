package de.raidcraft.api.database;

import java.sql.Connection;

/**
 * Author: Philip
 * Date: 16.09.12 - 15:03
 * Description:
 */
public abstract class Table {

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

        return Database.getConnection();
    }
}

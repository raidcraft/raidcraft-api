package de.raidcraft;

import io.ebean.annotation.Platform;
import io.ebean.dbmigration.DbMigration;
import org.junit.Test;

import java.io.IOException;

public class GenerateDbMigration {

    /**
     * Generate the next "DB schema DIFF" migration.
     */
    @Test
    public static void main(String[] args) throws IOException {


        DbMigration dbMigration = DbMigration.create();
        dbMigration.setPlatform(Platform.MYSQL);

        dbMigration.generateMigration();
    }
}
package com.gmail.furkanaxx34.ranks.storage.impl;

import com.gmail.furkanaxx34.ranks.storage.Sql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite implements Sql {

    private final String ranksTable = "ranks";

    private final File file;

    private Connection connection;

    public SQLite(File file) {
        this.file = file;
        if (!this.file.exists()) {
            try {
                this.file.getParentFile().mkdir();
                this.file.createNewFile();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + file);
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
            System.out.print("Failed to get sqlite driver");
        } catch (final SQLException e) {
            e.printStackTrace();
            System.out.print("Failed to create sqlite connection");
        }
        this.createTable();
    }

    @Override
    public Connection getConnection() {
        try {
            if (this.connection == null || this.connection.isClosed()) {
                try {
                    this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.file);
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return this.connection;
    }

    @Override
    public String getTableName() {
        return this.ranksTable;
    }

    private void createTable() {
        try {
            this.connection.createStatement()
                    .execute("CREATE TABLE IF NOT EXISTS " + this.ranksTable + "(uuid varchar(36) NOT NULL, player_name, prestige INTEGER NOT NULL DEFAULT(1), last_taken_money INTEGER NOT NULL DEFAULT(0), PRIMARY KEY (uuid))");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }
}

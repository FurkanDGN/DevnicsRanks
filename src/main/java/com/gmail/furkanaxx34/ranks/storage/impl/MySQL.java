package com.gmail.furkanaxx34.ranks.storage.impl;

import com.gmail.furkanaxx34.ranks.storage.Sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL implements Sql {

    private final String ranksTable;

    private final String database;

    private final String host;

    private final String password;

    private final int port;

    private final String username;

    private Connection connection;

    public MySQL(final String host, final int port, final String database, final String username, final String password, final String ranksTable) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.ranksTable = ranksTable;
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            this.createTable();
        } catch (final SQLException e) {
            throw new RuntimeException("Cannot connect to database!");
        }
    }

    @Override
    public Connection getConnection() {
        if (!this.isConnected()) {
            try {
                this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
        return this.connection;
    }

    public boolean isConnected() {
        try {
            return this.connection != null && !this.connection.isClosed() && this.connection.isValid(5);
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
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

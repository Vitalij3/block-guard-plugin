package me.salatosik.blockguardplugin.core;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private Connection connection;

    public Database(File file) {
        try {
            Class.forName("org.sqlite.JDBC");
            if(!file.exists()) if(!file.createNewFile()) throw new IOException("Failed create database file");
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());

            try(Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS blocks (x INT, y INT, z INT, uuid STRING, worldName STRING, blockName STRING)");
            }

        } catch(IOException | SQLException | ClassNotFoundException exception) { exception.printStackTrace(); connection = null; }
    }

    public void closeConnection() {
        if(verifyConnection()) try { connection.close(); } catch(SQLException exception) { exception.printStackTrace(); }
    }

    public boolean verifyConnection() {
        return connection != null;
    }

    private void putBlockValues(PlayerBlock playerBlock, PreparedStatement statement) throws SQLException {
        statement.setInt(1, playerBlock.x);
        statement.setInt(2, playerBlock.y);
        statement.setInt(3, playerBlock.z);
        statement.setString(4, playerBlock.uuid);
        statement.setString(5, playerBlock.worldName);
        statement.setString(6, playerBlock.blockName);
        statement.execute();
    }

    public List<PlayerBlock> getPlayerBlocks() {
        List<PlayerBlock> playerBlocks = new ArrayList<>();

        try(Statement statement = connection.createStatement()) {
            try(ResultSet resultSet = statement.executeQuery("SELECT * FROM blocks")) {
                while(resultSet.next()) {
                    PlayerBlock playerBlock = new PlayerBlock(resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"), resultSet.getString("uuid"), resultSet.getString("worldName"), resultSet.getString("blockName"));
                    playerBlocks.add(playerBlock);
                }
            }

        } catch(SQLException exception) { exception.printStackTrace(); }

        return playerBlocks;
    }

    public void addPlayerBlock(PlayerBlock playerBlock) {
        try(PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO blocks (x, y, z, uuid, worldName, blockName) VALUES(?, ?, ?, ?, ?, ?)")) {
            putBlockValues(playerBlock, preparedStatement);
        } catch(SQLException exception) { exception.printStackTrace(); }
    }

    public void removePlayerBlock(PlayerBlock playerBlock) {
        try(PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM blocks WHERE x = ? AND y = ? AND z = ? AND uuid = ? AND worldName = ? AND blockName = ?")) {
            putBlockValues(playerBlock, preparedStatement);
        } catch(SQLException exception) { exception.printStackTrace(); }
    }
}

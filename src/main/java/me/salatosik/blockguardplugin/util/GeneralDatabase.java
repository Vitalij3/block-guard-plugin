package me.salatosik.blockguardplugin.util;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GeneralDatabase {
    private Connection connection;

    public GeneralDatabase(File file) {
        try {
            Class.forName("org.sqlite.JDBC");
            if(!file.exists()) if(!file.createNewFile()) throw new IOException("Failed create database file");
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());

            try(Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS blocks (x INT, y INT, z INT, uuid STRING)");
            }

        } catch(IOException | SQLException | ClassNotFoundException exception) { exception.printStackTrace(); connection = null; }
    }

    public void closeConnection() {
        if(verifyConnection()) try { connection.close(); } catch(SQLException exception) { exception.printStackTrace(); }
    }

    public boolean verifyConnection() {
        return connection != null;
    }

    private void putBlockValues(PlayerBlock playerBlock, PreparedStatement statement, boolean execute) throws SQLException {
        statement.setInt(1, playerBlock.x);
        statement.setInt(2, playerBlock.y);
        statement.setInt(3, playerBlock.z);
        statement.setString(4, playerBlock.uuid);

        if(execute) statement.execute();
    }

    public List<PlayerBlock> getPlayerBlocks() {
        List<PlayerBlock> playerBlocks = new ArrayList<>();

        try(Statement statement = connection.createStatement()) {
            try(ResultSet resultSet = statement.executeQuery("SELECT * FROM blocks")) {
                while(resultSet.next()) {
                    PlayerBlock playerBlock = new PlayerBlock(resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"), resultSet.getString("uuid"));
                    playerBlocks.add(playerBlock);
                }
            }

        } catch(SQLException exception) { exception.printStackTrace(); }

        return playerBlocks;
    }

    public void addPlayerBlock(PlayerBlock playerBlock) {
        try(PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO blocks (x, y, z, uuid) VALUES(?, ?, ?, ?)")) {
            putBlockValues(playerBlock, preparedStatement, true);
        } catch(SQLException exception) { exception.printStackTrace(); }
    }

    public void removePlayerBlock(PlayerBlock playerBlock) {
        try(PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM blocks WHERE x = ? AND y = ? AND z = ? AND uuid = ?")) {
            putBlockValues(playerBlock, preparedStatement, true);
        } catch(SQLException exception) { exception.printStackTrace(); }
    }
}

package me.salatosik.blockguardplugin.util;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Collection;

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

    public <L extends Collection<PlayerBlock>> void addBlocks(L blocks) throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement("INSERT INTO blocks (x, y, z, uuid) VALUES(?, ?, ?, ?)")) {
            for(PlayerBlock playerBlock: blocks) {
                putBlockValues(playerBlock, statement, true);
            }
        }
    }

    public <L extends Collection<PlayerBlock>> void putPlayerBlocks(L list) throws SQLException {
        try(Statement statement = connection.createStatement()) {
            try(ResultSet resultSet = statement.executeQuery("SELECT * FROM blocks")) {
                while(resultSet.next()) {
                    PlayerBlock playerBlock = new PlayerBlock(resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"), resultSet.getString("uuid"));
                    list.add(playerBlock);
                }
            }
        }
    }

    public <L extends Collection<PlayerBlock>> void removePlayerBlocks(L list) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM blocks WHERE x = ? AND y = ? AND z = ? AND uuid = ?")) {
            for(PlayerBlock playerBlock: list) {
                putBlockValues(playerBlock, preparedStatement, true);
            }
        }
    }
}

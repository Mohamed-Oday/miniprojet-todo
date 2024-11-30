package org.openjfx.miniprojet.utiil;

import java.sql.*;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/accounts";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";
    private static Database instance;

    private Database() {

    }

    public static synchronized Database getInstance() {
        if (instance == null){
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public int executeUpdate(String sql, Object... params) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        setParameters(statement, params);
        return statement.executeUpdate();
    }

    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        setParameters(statement, params);
        return statement.executeQuery();
    }

    public void deleteSavedUser() throws SQLException {
        String sql = "DELETE FROM saveduser";
        executeUpdate(sql);
    }

    private void setParameters(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
    }
}

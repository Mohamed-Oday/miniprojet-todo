package org.openjfx.miniprojet.util;

import java.sql.*;

/**
 * Singleton class for managing database connections and executing SQL queries.
 * @author Sellami Mohamed Oday
 * @version 1.0
 * @since 1.0
 * @see Connection
 * @see DriverManager
 * @see PreparedStatement
 * @see ResultSet
 * @see SQLException
 */
public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/todo";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";
    private static Database instance;

    /**
     * Private constructor to prevent instantiation.
     */
    private Database() {

    }

    /**
     * Returns the singleton instance of the Database class.
     *
     * @return the singleton instance
     */
    public static synchronized Database getInstance() {
        if (instance == null){
            instance = new Database();
        }
        return instance;
    }

    /**
     * Establishes and returns a connection to the database.
     *
     * @return the database connection
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Executes an update SQL statement (INSERT, UPDATE, DELETE).
     *
     * @param sql the SQL statement to execute
     * @param params the parameters to set in the SQL statement
     * @return the number of rows affected
     * @throws SQLException if a database access error occurs
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        setParameters(statement, params);
        return statement.executeUpdate();
    }

    /**
     * Executes a query SQL statement (SELECT).
     *
     * @param sql the SQL statement to execute
     * @param params the parameters to set in the SQL statement
     * @return the result set of the query
     * @throws SQLException if a database access error occurs
     */
    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        setParameters(statement, params);
        return statement.executeQuery();
    }

    /**
     * Sets the parameters for a prepared statement.
     *
     * @param statement the prepared statement
     * @param params the parameters to set in the statement
     * @throws SQLException if a database access error occurs
     */
    private void setParameters(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
    }

    public Integer executeInsert(String sql, Object... params) throws SQLException {
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            preparedStatement.executeUpdate();

            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Failed to get generated key");
            }
        }
    }
}
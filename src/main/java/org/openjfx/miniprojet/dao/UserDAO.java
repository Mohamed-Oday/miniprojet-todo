package org.openjfx.miniprojet.dao;

import org.openjfx.miniprojet.utiil.Database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public ResultSet getSavedUser() throws SQLException {
        String query = "SELECT * FROM saveduser";
        return Database.getInstance().executeQuery(query);
    }
}

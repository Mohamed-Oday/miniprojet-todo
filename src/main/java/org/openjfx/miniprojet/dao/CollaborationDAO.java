package org.openjfx.miniprojet.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.openjfx.miniprojet.model.Permission;
import org.openjfx.miniprojet.util.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CollaborationDAO {

    public void addCollaboration(String category, String owner, String user, Permission permission) {
        try{
            ResultSet resultSet = Database.getInstance().executeQuery("SELECT category_name, owner_name, collaborated_user FROM category_collaboration WHERE category_name = ? AND owner_name = ? AND collaborated_user = ?", category, owner, user);
            if (resultSet.next()){
                throw new DataAccessException("Collaboration already exists for this user", null);
            }
            Database.getInstance().executeUpdate("INSERT INTO category_collaboration (category_name, owner_name, collaborated_user, permission) VALUES (?, ?, ?, ?)",category, owner, user, permission.toString());
        } catch (SQLException e){
            throw new DataAccessException("Error adding collaboration", e);
        }
    }

    public Permission getPermission(String category, String owner, String user) {
        try{
            ResultSet resultSet = Database.getInstance().executeQuery(
                    "SELECT permission FROM category_collaboration WHERE category_name = ? AND owner_name = ? AND collaborated_user = ?",
                    category, owner, user
            );
            if (!resultSet.next()){
                throw new DataAccessException("Collaboration does not exist", null);
            }
            return Permission.valueOf(resultSet.getString("permission"));
        } catch (SQLException e){
            throw new DataAccessException("Error getting permission", e);
        }
    }


    public void updatePermission(String category, String owner, String user, Permission permission) {
        try {
            Database.getInstance().executeUpdate(
                    "UPDATE category_collaboration SET permission = ? WHERE category_name = ? AND owner_name = ? AND collaborated_user = ?",
                    permission.toString(), category, owner, user
            );
        } catch (SQLException e) {
            throw new DataAccessException("Error updating permission", e);
        }
    }

    public ObservableList<String> getPendingInvites(String user){
        ObservableList<String> pendingInvites = FXCollections.observableArrayList();
        try{
            ResultSet resultSet = Database.getInstance().executeQuery(
                    "SELECT category_name, owner_name FROM category_collaboration "
                            + "WHERE collaborated_user = ? AND is_accepted = 0",
                    user
            );
            while (resultSet.next()){
                String category = resultSet.getString("category_name");
                String owner = resultSet.getString("owner_name");
                pendingInvites.add(category + " from " + owner);
            }
            return pendingInvites;
        } catch (SQLException e){
            throw new DataAccessException("Error getting pending invites", e);
        }
    }

    public void acceptInvite(String category, String owner, String user) {
        try {
            Database.getInstance().executeUpdate(
                    "UPDATE category_collaboration SET is_accepted = 1 " +
                            "WHERE category_name = ? AND owner_name = ? AND collaborated_user = ? AND is_accepted = 0",
                    category, owner, user
            );
        } catch (SQLException e) {
            throw new DataAccessException("Error accepting invite", e);
        }
    }

    public void declineInvite(String category, String owner, String user) {
        try {
            Database.getInstance().executeUpdate(
                    "DELETE FROM category_collaboration " +
                            "WHERE category_name = ? AND owner_name = ? AND collaborated_user = ? AND is_accepted = 0",
                    category, owner, user
            );
        } catch (SQLException e) {
            throw new DataAccessException("Error declining invite", e);
        }
    }

    public boolean hasInvites(String user) {
        try {
            System.out.println(user);
            ResultSet resultSet = Database.getInstance().executeQuery(
                    "SELECT COUNT(*) as invite_count FROM category_collaboration " +
                            "WHERE collaborated_user = ? AND is_accepted = 0",
                    user
            );
            if (resultSet.next()) {
                return resultSet.getInt("invite_count") > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new DataAccessException("Error checking for invites", e);
        }
    }

    public void removeCollaboration(String category, String owner, String Collaborated) {
        try {
            Database.getInstance().executeUpdate(
                    "DELETE FROM category_collaboration WHERE category_name = ? AND owner_name = ? AND collaborated_user = ?",
                    category, owner, Collaborated
            );
        } catch (SQLException e) {
            throw new DataAccessException("Error removing collaboration", e);
        }

    }

    public ObservableList<String> getCollaborators(String owner, String category) {
        ObservableList<String> collaborators = FXCollections.observableArrayList();
        System.out.println(owner + " " + category);
        try {
            ResultSet resultSet = Database.getInstance().executeQuery(
                    "SELECT collaborated_user FROM category_collaboration WHERE owner_name = ? AND category_name = ?",
                    owner, category
            );
            while (resultSet.next()) {
                collaborators.add(resultSet.getString("collaborated_user"));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting collaborators", e);
        }
        return collaborators;
    }

    public boolean isCollabCategory(String category, String owner){
        try{
            ResultSet resultSet = Database.getInstance().executeQuery(
                    "SELECT category_name FROM category_collaboration WHERE category_name = ? AND owner_name = ?",
                    category, owner
            );
            return resultSet.next();
        } catch (SQLException e){
            throw new DataAccessException("Error checking if category is a collaboration category", e);
        }
    }
}

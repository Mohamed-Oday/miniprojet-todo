package org.openjfx.miniprojet.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.openjfx.miniprojet.model.Permission;
import org.openjfx.miniprojet.util.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object (DAO) class for managing collaborations.
 * This class provides methods to add, update, and retrieve collaborations from the database.
 */
public class CollaborationDAO {

    /**
     * Adds a new collaboration for the specified category, owner, and user with the given permission.
     *
     * @param category the name of the category
     * @param owner the owner of the category
     * @param user the user to collaborate with
     * @param permission the permission level for the collaboration
     * @throws DataAccessException if an error occurs while adding the collaboration
     */
    public void addCollaboration(String category, String owner, String user, Permission permission) {
        try {
            ResultSet resultSet = Database.getInstance().executeQuery("SELECT category_name, owner_name, collaborated_user FROM category_collaboration WHERE category_name = ? AND owner_name = ? AND collaborated_user = ?", category, owner, user);
            if (resultSet.next()) {
                throw new DataAccessException("Collaboration already exists for this user", null);
            }
            Database.getInstance().executeUpdate("INSERT INTO category_collaboration (category_name, owner_name, collaborated_user, permission) VALUES (?, ?, ?, ?)", category, owner, user, permission.toString());
        } catch (SQLException e) {
            throw new DataAccessException("Error adding collaboration", e);
        }
    }

    /**
     * Retrieves the permission level for the specified category, owner, and user.
     *
     * @param category the name of the category
     * @param owner the owner of the category
     * @param user the user to get the permission for
     * @return the permission level
     * @throws DataAccessException if an error occurs while retrieving the permission
     */
    public Permission getPermission(String category, String owner, String user) {
        try {
            ResultSet resultSet = Database.getInstance().executeQuery(
                    "SELECT permission FROM category_collaboration WHERE category_name = ? AND owner_name = ? AND collaborated_user = ?",
                    category, owner, user
            );
            if (!resultSet.next()) {
                throw new DataAccessException("Collaboration does not exist", null);
            }
            return Permission.valueOf(resultSet.getString("permission"));
        } catch (SQLException e) {
            throw new DataAccessException("Error getting permission", e);
        }
    }

    /**
     * Updates the permission level for the specified category, owner, and user.
     *
     * @param category the name of the category
     * @param owner the owner of the category
     * @param user the user to update the permission for
     * @param permission the new permission level
     * @throws DataAccessException if an error occurs while updating the permission
     */
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

    /**
     * Retrieves the list of pending collaboration invites for the specified user.
     *
     * @param user the user to get the pending invites for
     * @return an observable list of pending invites
     * @throws DataAccessException if an error occurs while retrieving the pending invites
     */
    public ObservableList<String> getPendingInvites(String user) {
        ObservableList<String> pendingInvites = FXCollections.observableArrayList();
        try {
            ResultSet resultSet = Database.getInstance().executeQuery(
                    "SELECT category_name, owner_name FROM category_collaboration "
                            + "WHERE collaborated_user = ? AND is_accepted = 0",
                    user
            );
            while (resultSet.next()) {
                String category = resultSet.getString("category_name");
                String owner = resultSet.getString("owner_name");
                pendingInvites.add(category + " from " + owner);
            }
            return pendingInvites;
        } catch (SQLException e) {
            throw new DataAccessException("Error getting pending invites", e);
        }
    }

    /**
     * Accepts a collaboration invite for the specified category, owner, and user.
     *
     * @param category the name of the category
     * @param owner the owner of the category
     * @param user the user accepting the invite
     * @throws DataAccessException if an error occurs while accepting the invite
     */
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

    /**
     * Declines a collaboration invite for the specified category, owner, and user.
     *
     * @param category the name of the category
     * @param owner the owner of the category
     * @param user the user declining the invite
     * @throws DataAccessException if an error occurs while declining the invite
     */
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

    /**
     * Checks if the specified user has any pending collaboration invites.
     *
     * @param user the user to check for invites
     * @return true if the user has pending invites, false otherwise
     * @throws DataAccessException if an error occurs while checking for invites
     */
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

    /**
     * Removes a collaboration for the specified category, owner, and collaborated user.
     *
     * @param category the name of the category
     * @param owner the owner of the category
     * @param collaborated the user to remove from the collaboration
     * @throws DataAccessException if an error occurs while removing the collaboration
     */
    public void removeCollaboration(String category, String owner, String collaborated) {
        try {
            Database.getInstance().executeUpdate(
                    "DELETE FROM category_collaboration WHERE category_name = ? AND owner_name = ? AND collaborated_user = ?",
                    category, owner, collaborated
            );
        } catch (SQLException e) {
            throw new DataAccessException("Error removing collaboration", e);
        }
    }

    /**
     * Retrieves the list of collaborators for the specified category and owner.
     *
     * @param owner the owner of the category
     * @param category the name of the category
     * @return an observable list of collaborators
     * @throws DataAccessException if an error occurs while retrieving the collaborators
     */
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

    /**
     * Checks if the specified category and owner represent a collaboration category.
     *
     * @param category the name of the category
     * @param owner the owner of the category
     * @return true if the category is a collaboration category, false otherwise
     * @throws DataAccessException if an error occurs while checking the category
     */
    public boolean isCollabCategory(String category, String owner) {
        try {
            ResultSet resultSet = Database.getInstance().executeQuery(
                    "SELECT category_name FROM category_collaboration WHERE category_name = ? AND owner_name = ?",
                    category, owner
            );
            return resultSet.next();
        } catch (SQLException e) {
            throw new DataAccessException("Error checking if category is a collaboration category", e);
        }
    }
}
package org.openjfx.miniprojet.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a task with a name, description, due date, status, and comments.
 */
public class TaskImpl implements Task {

    private int id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Status status;
    private final List<String> comments;
    private String priority;
    private String category;
    private boolean important;
    private String owner;
    private int reminder;

    /**
     * Constructs a new TaskImpl with the specified name, description, due date, and status.
     *
     * @param name the name of the task
     * @param description the description of the task
     * @param dueDate the due date of the task
     * @param status the status of the task
     * @param priority the priority of the task
     * @param category the category of the task
     * @param startDate the start date of the task
     * @param important whether the task is important
     * @param owner the owner of the task
     * @param reminder the reminder times for the task
     */
    public TaskImpl(String name, String description, LocalDate dueDate, Status status, String priority, String category, LocalDate startDate, boolean important, String owner, int reminder) {
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.comments = new ArrayList<>();
        this.priority = priority;
        this.category = category;
        this.startDate = startDate;
        this.important = important;
        this.owner = owner;
        this.reminder = reminder;
    }

    /**
     * Gets the reminder time for the task.
     *
     * @return the reminder time
     */
    public int getReminder() {
        return reminder;
    }

    /**
     * Sets the reminder time for the task.
     *
     * @param reminder the new reminder time
     */
    public void setReminder(int reminder) {
        this.reminder = reminder;
    }

    /**
     * Gets the owner of the task.
     *
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets whether the task is important.
     *
     * @param important the new importance status
     */
    public void setImportant(boolean important){
        this.important = important;
    }

    /**
     * Checks if the task is important.
     *
     * @return true if the task is important, false otherwise
     */
    public boolean isImportant(){
        return important;
    }

    /**
     * Sets the category of the task.
     *
     * @param category the new category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the category of the task.
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Adds a comment to the task.
     *
     * @param comment the comment to add
     */
    public void addComment(String comment) {
        comments.add(comment);
    }

    /**
     * Returns the list of comments for the task.
     *
     * @return the list of comments
     */
    public List<String> getComments() {
        return comments;
    }

    /**
     * Returns the name of the task.
     *
     * @return the name of the task
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the task.
     *
     * @param name the new name of the task
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the description of the task.
     *
     * @return the description of the task
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the task.
     *
     * @param description the new description of the task
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the start date of the task.
     *
     * @param startDate the new start date
     */
    private void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the start date of the task.
     *
     * @return the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the due date of the task.
     *
     * @return the due date of the task
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date of the task.
     *
     * @param dueDate the new due date of the task
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Returns the status of the task.
     *
     * @return the status of the task
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Edits the task with the specified name, description, due date, and status.
     *
     * @param name the new name of the task
     * @param description the new description of the task
     * @param dueDate the new due date of the task
     * @param priority the new priority of the task
     * @param category the new category of the task
     * @param startDate the new start date of the task
     * @param status the new status of the task
     * @param reminder the new reminder time of the task
     */
    public void editTask(String name, String description, LocalDate dueDate, String priority, String category, LocalDate startDate, Status status, int reminder) {
        setId(id);
        setName(name);
        setDescription(description);
        setDueDate(dueDate);
        setPriority(priority);
        setCategory(category);
        setStartDate(startDate);
        setReminder(reminder);
        changeStatus(status);
    }

    /**
     * Gets the priority of the task.
     *
     * @return the priority
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Sets the priority of the task.
     *
     * @param priority the new priority
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * Changes the status of the task.
     *
     * @param status the new status of the task
     */
    public void changeStatus(Status status) {
        this.status = status;
    }

    /**
     * Gets the ID of the task.
     *
     * @return the ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the task.
     *
     * @param id the new ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns a string representation of the task.
     *
     * @return a string representation of the task
     */
    @Override
    public String toString() {
        return getName();
    }
}
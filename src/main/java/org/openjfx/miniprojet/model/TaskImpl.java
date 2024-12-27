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

    /**
     * Constructs a new TaskImpl with the specified name, description, due date, and status.
     *
     * @param name the name of the task
     * @param description the description of the task
     * @param dueDate the due date of the task
     * @param status the status of the task
     */
    public TaskImpl(String name, String description, LocalDate dueDate, Status status, String priority, String category, LocalDate startDate, boolean important, String owner) {
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
    }

    public String getOwner() {
        return owner;
    }

    public void setImportant(boolean important){
        this.important = important;
    }

    public boolean isImportant(){
        return important;
    }

    public void setCategory(String category) {
        this.category = category;
    }

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

    private void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

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
     */
    public void editTask(String name, String description, LocalDate dueDate, String priority, String category, LocalDate startDate, Status status) {
        setId(id);
        setName(name);
        setDescription(description);
        setDueDate(dueDate);
        setPriority(priority);
        setCategory(category);
        setStartDate(startDate);
        changeStatus(status);
    }

    public String getPriority() {
        return priority;
    }

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

    public int getId() {
        return id;
    }

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
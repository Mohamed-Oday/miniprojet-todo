package org.openjfx.miniprojet.model;

import java.time.LocalDate;

/**
 * Interface representing a task.
 */
public interface Task {
    /**
     * Edits the task with the provided details.
     *
     * @param name the name of the task.
     * @param description the description of the task.
     * @param dueDate the due date of the task.
     * @param priority the priority of the task.
     * @param category the category of the task.
     * @param startDate the start date of the task.
     * @param status the status of the task.
     * @param reminder the reminder time for the task.
     */
    void editTask(String name, String description, LocalDate dueDate, String priority, String category, LocalDate startDate, Status status, int reminder);

    /**
     * Changes the status of the task.
     *
     * @param status the new status of the task.
     */
    void changeStatus(Status status);
}
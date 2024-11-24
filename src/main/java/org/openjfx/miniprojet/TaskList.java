package org.openjfx.miniprojet;

import java.util.List;

/**
 * Abstract class representing a list of tasks.
 */
public abstract class TaskList {

    /**
     * Adds a task to the task list.
     *
     * @param task the task to add
     */
    public abstract void addTask(TaskImpl task);

    /**
     * Removes a task from the task list.
     *
     * @param task the task to remove
     */
    public abstract void deleteTask(TaskImpl task);

    /**
     * Edits a task in the task list.
     *
     * @param task the task to edit
     */
    public abstract void editTask(TaskImpl task);

    /**
     * Displays the list of tasks.
     *
     * @return a list of tasks
     */
    public abstract List<TaskImpl> displayTasks();
}
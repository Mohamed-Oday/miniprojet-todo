package org.openjfx.miniprojet;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * TaskListImpl is a concrete implementation of the TaskList class.
 * It manages a list of TaskImpl objects and provides methods to add, delete, edit, display, search, filter, sort, export, and import tasks.
 * Note: This class uses Gson for JSON serialization and deserialization
 *
 * @see TaskList
 * @see TaskImpl
 * @see Status
 *
 * @version 1.0
 * @author Sellami Mohamed Odai
 */
public class TaskListImpl extends TaskList{

    private List<TaskImpl> tasks;
    private List<String> categories;
    public TaskListImpl() {
        this.tasks = new ArrayList<TaskImpl>();
        this.categories = new ArrayList<String>();
    }

    /**
     * Adds a task to the task list.
     *
     * @param task the task to be added
     */
    @Override
    public void addTask(TaskImpl task) {
        tasks.add(task);
    }

    /**
     * Deletes the specified task from the task list.
     *
     * @param task the task to be deleted
     */
    @Override

    public void deleteTask(TaskImpl task) {
        tasks.remove(task);
    }

    /**
     * Edits the specified task with its current details.
     *
     * @param task the task to be edited
     */
    @Override
    public void editTask(TaskImpl task) {
        task.editTask(task.getName(), task.getDescription(), task.getDueDate(), task.getStatus());
    }

    /**
     * Returns the list of tasks.
     * @return
     *
     * @return a list of TaskImpl objects representing the tasks.
     */
    @Override
    public List<TaskImpl> displayTasks() {
        return tasks;
    }


    /**
     *
     * Searches for tasks that contain the keyword in either the name or description
     *
     * @param keyword the keyword to search for
     * @return returning a task list that contain the same keyword in the name or description
     */
    public List<TaskImpl> searchTasks(String keyword) {
        List<TaskImpl> matchingTasks = new ArrayList<>();
        if (keyword.isEmpty() || keyword == null) {
            return null;
        }

        for (TaskImpl task: tasks) {
            if (task.getName() != null && task.getName().toLowerCase().contains(keyword.toLowerCase())) {
                matchingTasks.add(task);
            } else if (task.getDescription() != null && task.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }


    /**
     *
     * Filter tasks by a certain status (completed/abandoned/in_progress)
     *
     * @param status the status to filter the tasks by
     * @return returning a task list filtered by status
     */
    public List<TaskImpl> filterTasks(Status status) {
        List<TaskImpl> matchingTasks = new ArrayList<>();
        for (TaskImpl task: tasks) {
            if (task.getStatus().equals(status)){
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /**
     * 	Sort tasks by their due date and display them
     * 	@return returning a task list sorted by due date
     */
    public List<TaskImpl> sortTaskByDueDate() {
        List<TaskImpl> sortedTasks = new ArrayList<>();
        Collections.sort(sortedTasks, Comparator.comparing(TaskImpl::getDueDate));
        return sortedTasks;
    }


    /**
     * Exports the list of tasks to a specified file in JSON format.
     *
     * @param filePath the path of the file where the tasks will be exported
     */
    public void exportTasks(String filePath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try(FileWriter writer = new FileWriter(filePath)){
            gson.toJson(tasks, writer);
            System.out.println("Tasks exported to: " +filePath);
        } catch (IOException e) {
            System.err.println("An error occurred while exporting tasks: " + e.getMessage());
        }
    }

    /**
     * Imports tasks from a JSON file specified by the given file path.
     * The tasks are read from the file and added to the existing task list.
     *
     * @param filePath the path to the JSON file containing the tasks to import
     */
    public void importTasks(String filePath) {
        Gson gson = new Gson();
        try(FileReader reader = new FileReader(filePath)){
            Type taskListType = new TypeToken<ArrayList<TaskImpl>>() {}.getType();
            List<TaskImpl> importedTasks = gson.fromJson(reader, taskListType);
            if (importedTasks != null) {
                tasks.addAll(importedTasks);
                System.out.println("Tasks imported from: "+filePath);
            }

        } catch(IOException e) {
            System.err.println("An error occurred while importing tasks: " + e.getMessage());
        }
    }

}


package org.openjfx.miniprojet.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import org.openjfx.miniprojet.dao.TaskDAO;
import org.openjfx.miniprojet.model.Status;
import org.openjfx.miniprojet.model.TaskImpl;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service class for handling task reminders.
 * This class periodically checks for tasks that are due and sends reminders to the user.
 */
public class TaskReminderService {
    private final TaskDAO taskDAO;
    private final AppController appController;
    private final ConcurrentHashMap<Integer, LocalDateTime> lastReminderSent;

    private ScheduledService<Void> reminderService;

    /**
     * Constructs a TaskReminderService with the specified TaskDAO and AppController.
     *
     * @param taskDAO the TaskDAO to interact with the database
     * @param appController the AppController to handle UI updates
     */
    public TaskReminderService(TaskDAO taskDAO, AppController appController) {
        this.taskDAO = taskDAO;
        this.appController = appController;
        this.lastReminderSent = new ConcurrentHashMap<>();
        initializeReminderService();
    }

    /**
     * Initializes the reminder service.
     * Sets up a scheduled service to check for due tasks every 15 minutes.
     */
    private void initializeReminderService() {
        reminderService = new ScheduledService<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        checkAndSendReminders();
                        return null;
                    }
                };
            }
        };
        reminderService.setPeriod(Duration.minutes(15)); // Check every 15 minutes
        reminderService.setRestartOnFailure(true);
    }

    /**
     * Starts the reminder service.
     * Restarts the scheduled service to begin checking for due tasks.
     */
    public void startReminderService() {
        reminderService.restart();
    }

    /**
     * Stops the reminder service.
     * Cancels the scheduled service to stop checking for due tasks.
     */
    public void stopReminderService() {
        reminderService.cancel();
    }

    /**
     * Checks for tasks that are due and sends reminders to the user.
     * Loads the tasks from the database and sends a notification if a task is due.
     */
    private void checkAndSendReminders() {
        String userID = appController.getUser();
        ObservableList<TaskImpl> tasks = taskDAO.loadTasks(userID, "Tasks", null);
        LocalDateTime now = LocalDateTime.now();
        for (TaskImpl task : tasks) {
            if (shouldSendReminder(task, now)) {
                Platform.runLater(() -> {
                    appController.showNotification(
                            "Task Reminder",
                            "Reminder",
                            "is due " + task.getDueDate().toString(),
                            task.getName()
                    );
                });
                lastReminderSent.put(task.getId(), now);
            }
        }
    }

    /**
     * Determines if a reminder should be sent for the specified task.
     *
     * @param task the task to check
     * @param now the current date and time
     * @return true if a reminder should be sent, false otherwise
     */
    private boolean shouldSendReminder(TaskImpl task, LocalDateTime now) {
        if (task.getReminder() == 0 || task.getStatus().equals(Status.Completed)) {
            return false;
        }

        LocalDateTime dueDate = task.getDueDate().atStartOfDay();
        LocalDateTime lastReminder = lastReminderSent.get(task.getId());

        if (lastReminder == null) {
            return now.isAfter(dueDate.minusDays(7)) && now.isBefore(dueDate);
        }

        long hoursSinceLastReminder = java.time.Duration.between(lastReminder, now).toHours();
        int reminderInterval = 168 / task.getReminder(); // 168 hours in a week

        return hoursSinceLastReminder >= reminderInterval &&
                now.isAfter(dueDate.minusDays(7)) &&
                now.isBefore(dueDate);
    }

    /**
     * Resets the reminder for the specified task.
     * Removes the last reminder sent time for the task.
     *
     * @param taskID the ID of the task to reset the reminder for
     */
    public void resetReminder(int taskID) {
        lastReminderSent.remove(taskID);
    }
}
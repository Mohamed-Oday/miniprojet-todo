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

public class TaskReminderService {
    private final TaskDAO taskDAO;
    private final AppController appController;
    private final ConcurrentHashMap<Integer, LocalDateTime> lastReminderSent;

    private ScheduledService<Void> reminderService;

    public TaskReminderService(TaskDAO taskDAO, AppController appController) {
        this.taskDAO = taskDAO;
        this.appController = appController;
        this.lastReminderSent = new ConcurrentHashMap<>();
        initializeReminderService();
    }

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

    public void startReminderService() {
        reminderService.restart();
    }

    public void stopReminderService() {
        reminderService.cancel();
    }

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

    public void resetReminder(int taskID) {
        lastReminderSent.remove(taskID);
    }
}
package org.openjfx.miniprojet.model;

import java.time.LocalDate;

public interface Task {
    void editTask(String name, String description, LocalDate dueDate, String priority, String category, LocalDate startDate);
    void changeStatus(Status status);
}

package org.openjfx.miniprojet.model;

import java.time.LocalDate;

public interface Task {
    void editTask(String name, String description, LocalDate dueDate, Status status);
    void changeStatus(Status status);
}

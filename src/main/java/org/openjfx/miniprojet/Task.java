package org.openjfx.miniprojet;

import java.time.LocalDate;

public interface Task {
    void editTask(String name, String description, LocalDate dueDate, Status status);
    void changeStatus(Status status);
}

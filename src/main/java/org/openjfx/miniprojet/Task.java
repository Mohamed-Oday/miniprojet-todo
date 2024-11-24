package org.openjfx.miniprojet;

import java.time.LocalDate;

public interface Task {
    public void editTask(String name, String description, LocalDate dueDate, Status status);
    public void changeStatus(Status status);
}

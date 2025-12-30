package edu.course.eventplanner.service;

import edu.course.eventplanner.model.Task;
import java.util.*;

public class TaskManager {
    private final Queue<Task> upcoming = new LinkedList<>();
    private final Stack<Task> completed = new Stack<>();
    public void addTask(Task task) {
        /* TODO */
    }
    public Task executeNextTask() {
        return null;
    }
    public Task undoLastTask() {
        return null;
    }
    public int remainingTaskCount() {
        return upcoming.size();
    }
}

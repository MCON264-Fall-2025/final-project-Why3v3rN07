package edu.course.eventplanner.service;

import edu.course.eventplanner.model.Task;
import java.util.*;

public class TaskManager {
    private final Deque<Task> upcoming = new ArrayDeque<>();
    private final Deque<Task> completed = new ArrayDeque<>();

    public void addTask(Task task) {
        upcoming.addLast(task);
    }
    public Task executeNextTask() {
        if (upcoming.isEmpty()) return null;
        completed.push(upcoming.poll());
        return completed.peek();
    }
    public Task undoLastTask() {
        if (completed.isEmpty()) return null;
        Task undone = completed.pop();
        upcoming.addLast(undone);
        return undone;
    }
    public int remainingTaskCount() {
        return upcoming.size();
    }
    public int completedTaskCount() {
        return completed.size();
    }
}

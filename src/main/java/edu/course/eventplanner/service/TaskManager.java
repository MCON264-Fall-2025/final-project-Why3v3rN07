package edu.course.eventplanner.service;

import edu.course.eventplanner.model.Task;
import java.util.*;

public class TaskManager {
    private final Queue<Task> upcoming = new LinkedList<>();
    private final Stack<Task> completed = new Stack<>();

    public void addTask(Task task) {
        upcoming.add(task);
    }
    public Task executeNextTask() {
        completed.push(upcoming.poll()); //maybe rather do remove, and return a message if no task available?
        return completed.peek();
    }
    public Task undoLastTask() {
        upcoming.add(completed.pop()); //again, need to handle no task available
        return upcoming.peek();
    }
    public int remainingTaskCount() {
        return upcoming.size();
    }
    public int completedTaskCount() {
        return completed.size();
    }
}

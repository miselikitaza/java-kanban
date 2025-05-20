package tasks;

import java.util.ArrayList;

public class Epic extends tasks.Task {

    private ArrayList<Integer> subtasks;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        this.subtasks = new ArrayList<>();
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW);
        this.subtasks = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }


    public void addSubtasks(int subtask) {
        this.subtasks.add(subtask);
    }

    public void removeSubtasks(int subtask) {
        this.subtasks.remove(Integer.valueOf(subtask));
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtasks=" + subtasks +
                '}';
    }
}
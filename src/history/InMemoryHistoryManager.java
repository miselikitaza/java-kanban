package history;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    Map<Integer, Node> nodes = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (nodes.containsKey(task.getId())) {
            Node node = nodes.get(task.getId());
            removeNode(node);
        }
        linkLast(task);
        nodes.put(task.getId(), tail);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node node = nodes.get(id);
        if (node == null) {
            return;
        }
        removeNode(node);
        nodes.remove(id, node);
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        node.next = null;
        node.prev = null;
    }

    private void linkLast(Task task) {
        Node node = new Node(task);
        if (head == null) {
            head = tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node currentNode = head;
        while (currentNode != null) {
            tasks.add(currentNode.value);
            currentNode = currentNode.next;
        }
        return tasks;
    }

    public static class Node {
        private Task value;
        private Node prev;
        private Node next;

        public Node(Task value) {
            this.value = value;
        }
    }
}

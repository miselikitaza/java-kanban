package main;

import manager.TaskManager;
import manager.Managers;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;


public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        //Создаем первую задачу:
        Task washingMachine = new Task("Стиральная машина", "подключить стиральную машину",
                TaskStatus.NEW);
        taskManager.createTask(washingMachine);

        //Создаем вторую задачу:
        Task table = new Task("Рабочий стол", "Собрать новый стол в кабинет", TaskStatus.NEW);
        taskManager.createTask(table);

        //Создаем эпик с двумя подзадачами:
        Epic birthday = new Epic("День рождения мамы", "Организовать праздник");
        taskManager.createEpic(birthday);

        //Первая подзадача:
        Subtask decoration = new Subtask("Украшения", "Украсить комнату",
                TaskStatus.IN_PROGRESS, birthday.getId());
        taskManager.createSubtask(decoration);

        //Вторая подзадача:
        Subtask cake = new Subtask("Торт", "Заказать праздничный торт",
                TaskStatus.NEW, birthday.getId());
        taskManager.createSubtask(cake);

        //Создаем эпик с одной подзадачей:
        Epic journey = new Epic("Путешествие", "Организовать поездку в США");
        taskManager.createEpic(journey);

        //Создаем подзадачу:
        Subtask ticket = new Subtask("Билеты", "Купить билеты на самолет",
                TaskStatus.NEW, journey.getId());
        taskManager.createSubtask(ticket);

        //Печатаем списки всех задач, эпиков и подзадач:
        System.out.println("Все задачи:");
        System.out.println(taskManager.getAllTasks());
        System.out.println();
        System.out.println("Все эпики:");
        System.out.println(taskManager.getAllEpics());
        System.out.println();
        System.out.println("Все подзадачи:");
        System.out.println(taskManager.getAllSubtask());
        System.out.println();

        //Меняем статусы созданных объектов
        Task newWashingMachine = new Task(washingMachine.getId(), washingMachine.getName(),
                washingMachine.getDescription(), TaskStatus.DONE);
        taskManager.updateTask(newWashingMachine);

        Task newTable = new Task(table.getId(), table.getName(), table.getDescription(), TaskStatus.IN_PROGRESS);
        taskManager.updateTask(newTable);

        Subtask newDecoration = new Subtask(decoration.getId(), decoration.getName(), decoration.getDescription(),
                TaskStatus.DONE, birthday.getId());
        taskManager.updateSubtask(newDecoration);

        Subtask newCake = new Subtask(cake.getId(), cake.getName(), cake.getDescription(),
                TaskStatus.DONE, birthday.getId());
        taskManager.updateSubtask(newCake);

        Subtask newTicket = new Subtask(ticket.getId(), ticket.getName(), ticket.getDescription(),
                TaskStatus.IN_PROGRESS, journey.getId());
        taskManager.updateSubtask(newTicket);

        System.out.println("Изменили статусы задач.");
        System.out.println("Все задачи:");
        System.out.println(taskManager.getAllTasks());
        System.out.println();
        System.out.println("Все эпики:");
        System.out.println(taskManager.getAllEpics());
        System.out.println();
        System.out.println("Все подзадачи:");
        System.out.println(taskManager.getAllSubtask());
        System.out.println();

        //Удаляем одну задачу и один эпик:
        taskManager.deleteTaskById(newWashingMachine.getId());
        taskManager.deleteEpicById(journey.getId());

        System.out.println("Удалили некоторые задачи.");
        System.out.println("Все задачи:");
        System.out.println(taskManager.getAllTasks());
        System.out.println();
        System.out.println("Все эпики:");
        System.out.println(taskManager.getAllEpics());
        System.out.println();
        System.out.println("Все подзадачи:");
        System.out.println(taskManager.getAllSubtask());
    }
}

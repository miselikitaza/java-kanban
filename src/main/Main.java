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

        System.out.println("Добавляем задачи в историю:");

        Task task1 = new Task("1", "1", TaskStatus.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("2", "2", TaskStatus.NEW);
        taskManager.createTask(task2);
        Task task3 = new Task("3", "3", TaskStatus.NEW);
        taskManager.createTask(task3);
        Task task4 = new Task("4", "4", TaskStatus.NEW);
        taskManager.createTask(task4);
        Task task5 = new Task("5", "5", TaskStatus.NEW);
        taskManager.createTask(task5);
        Task task6 = new Task("6", "6", TaskStatus.NEW);
        taskManager.createTask(task6);
        Task task7 = new Task("7", "7", TaskStatus.NEW);
        taskManager.createTask(task7);
        Task task8 = new Task("8", "8", TaskStatus.NEW);
        taskManager.createTask(task8);
        Task task9 = new Task("9", "9", TaskStatus.NEW);
        taskManager.createTask(task9);
        Task task10 = new Task("10", "10", TaskStatus.NEW);
        taskManager.createTask(task10);
        Task task11 = new Task("11", "11", TaskStatus.NEW);
        taskManager.createTask(task11);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.getTaskById(task4.getId());
        taskManager.getTaskById(task5.getId());
        taskManager.getTaskById(task6.getId());
        taskManager.getTaskById(task7.getId());
        taskManager.getTaskById(task8.getId());
        taskManager.getTaskById(task9.getId());
        taskManager.getTaskById(task10.getId());
        taskManager.getTaskById(task11.getId());

        System.out.println(taskManager.getHistory());
    }
}

package odevler.odev2;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class ProductOwner extends TeamMember {

    public ProductOwner(int teamSize, int sprintCount) {
        super(teamSize, "ProductOwner", sprintCount);
    }

    @Override
    public void operate() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        System.out.println("Connecting database ...");

        try {
            String url;
            url = "jdbc:mysql://localhost:3306/yazm457hw2";
            String username = "root";
            String password = "root";
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                System.out.println("Database connected!");

                // Veri tabanındaki product_backlog tablosuna (teamSize-2) kadar yeni task ekler
                // Her geliştiriciye bir task düşer, geliştiriciler bunları rastgele seçebilir
                for (int i = 0; i < teamSize - 2; i++) {
                    Task task = Task.generateTask(i);
                    addTaskToProductBacklog(connection, task);
                }

            }
            System.out.println("Connection closed!");
        } catch (SQLException e) {
           // throw new IllegalStateException("Cannot connect the database!", e);
        }
    }

    private void addTaskToProductBacklog(Connection connection, Task task) throws SQLException {
        String sql = "INSERT INTO product_backlog (task_name, backlog_id, priority) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, task.name);
            statement.setInt(2, task.backlogId);
            statement.setInt(3, task.priority);
            statement.executeUpdate();
        }
    }

    public static class Task {

        String name;
        int backlogId;
        int priority;

        public Task(String name, int backlogId, int priority) {
            this.name = name;
            this.backlogId = backlogId;
            this.priority = priority;
        }

        public static Task generateTask(int backlogId) {
            String[] tasks = {"task1", "task2", "task3"}; // Örnek görev isimleri
            Random random = new Random();
            int index = random.nextInt(tasks.length);
            String taskName = tasks[index];

            int priority = random.nextInt(10);

            return new Task(taskName, backlogId, priority);
        }
    }

    @Override
    public void run() {
        for (int i = 1; i <= sprintCount; i++) {
            System.out.println(threadName + " (sprint" + i + ")");
            try {
                operate();
                Thread.sleep(1000);
            } catch (InterruptedException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println(threadName + " bitti...");
    }
}

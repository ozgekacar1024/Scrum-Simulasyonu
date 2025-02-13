package odevler.odev2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Developer extends TeamMember {

    public Developer(int teamSize, String threadName, int sprintCount) {
        super(teamSize, threadName, sprintCount);
    }

    @Override

    public void operate() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        System.out.println(threadName + " is working on a task...");

        try {
            String url = "jdbc:mysql://localhost:3306/yazm457hw2";
            String username = "root";
            String password = "root";
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected!");

            // Sprint Backlog'tan bir görev al
            Task task = getTaskFromSprintBacklog(connection);

            // Görevi tamamla
            assert task != null;
            completeTask(connection, task);

            // Tamamlanan görevi Board'a yaz
            addToBoard(connection, task);

            connection.close();
            System.out.println("Connection closed!");
        } catch (SQLException e) {

        }
    }

    private Task getTaskFromSprintBacklog(Connection connection) throws SQLException {
        String sql = "SELECT * FROM sprint_backlog WHERE completed = false ORDER BY RAND() LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return new Task(
                        resultSet.getString("task_name"),
                        resultSet.getInt("backlog_id"),
                        resultSet.getInt("priority")
                );
            }
        }

        return null;
    }

    private void completeTask(Connection connection, Task task) throws SQLException {
        // Görev tamamlandı olarak işaretle veya gerekirse sprint_backlog tablosundan sil
        String updateSql = "UPDATE sprint_backlog SET completed = true WHERE backlog_id = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
            updateStatement.setInt(1, task.backlogId);
            updateStatement.executeUpdate();
        }
    }

    private void addToBoard(Connection connection, Task task) throws SQLException {
        // Tamamlanan görevi Board tablosuna ekle
        String insertSql = "INSERT INTO board (task_name, backlog_id, priority) VALUES (?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
            insertStatement.setString(1, task.name);
            insertStatement.setInt(2, task.backlogId);
            insertStatement.setInt(3, task.priority);
            insertStatement.executeUpdate();
        }
    }

    @Override
    public void run() {

            for (int i = 1; i <= this.sprintCount; i++) {
                try {

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(threadName + " bitti...");

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
    }
}

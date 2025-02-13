package odevler.odev2;

import java.sql.*;

public class ScrumMaster extends TeamMember {

    public ScrumMaster(int teamSize, int sprintCount) {
        super(teamSize, "ScrumMaster", sprintCount);
    }

    @Override
    public void operate() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        System.out.println("Connecting database ...");

        try {
            String url = "jdbc:mysql://localhost:3306/yazm457hw2";
            String username = "root";
            String password = "root";
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected!");

            // Veri tabanındaki sprint_backlog tablosuna (teamSize-2) kadar yeni task ekler
            for (int i = 0; i < teamSize - 2; i++) {
                Task task = getTaskFromProductBacklog(connection);
                assert task != null;
                addTaskToSprintBacklog(connection, task);
            }

            connection.close();
            System.out.println("Connection closed!");
        } catch (SQLException e) {
            //throw new IllegalStateException("Cannot connect the database!", e);
        }
    }

    private Task getTaskFromProductBacklog(Connection connection) throws SQLException {
        String sql = "INSERT INTO sprint_backlog (taskname, backlogId, sprintId, priority) VALUES (?, ?, ?, ?)";
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

    private void addTaskToSprintBacklog(Connection connection, Task task) throws SQLException {
        String sql = "INSERT INTO sprint_backlog (task_name, backlog_id, priority) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, task.name);
            statement.setInt(2, task.backlogId);
            statement.setInt(3, task.priority);
            statement.executeUpdate();
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

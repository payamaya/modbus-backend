import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseSetup {
    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS temperature  ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "temp REAL NOT NULL, "
                + "date TEXT NOT NULL)";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table created successfully!");
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTable();
    }
}

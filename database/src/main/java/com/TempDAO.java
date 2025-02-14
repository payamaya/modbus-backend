import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TempDAO {
    public static void insertTemp(double temp) {
        String sql = "INSERT INTO temperature(temp, date) VALUES(?, ?)";

        // Get current date & time
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, temp);
            pstmt.setString(2, currentDate);
            pstmt.executeUpdate();

            // Use simple text instead of special characters
            System.out.println("Temp recorded: " + temp + " degrees Celsius on " + currentDate);
        } catch (SQLException e) {
            System.out.println(" Error inserting temp: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        insertTemp(36.5);
    }
}

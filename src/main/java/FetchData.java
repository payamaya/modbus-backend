import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class FetchData {
    public static void getTemperatureData() {
        String sql = "SELECT * FROM temperature";  // Adjust table name if different

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("✅ Temperature Records:");
            while (rs.next()) {
                int id = rs.getInt("id");  // Assuming 'id' is the primary key
                double temp = rs.getDouble("temp");
                String date = rs.getString("date");
                System.out.println("ID: " + id + ", Temp: " + temp + "°C, Date: " + date);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        getTemperatureData();
    }
}

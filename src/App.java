import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;


public class App {

    private static Connection connection;

    // Establish database connection
    public  Connection getConnection() throws SQLException {
        try {
            // Database credentials
            String dbUrl = "jdbc:mysql://localhost:3306/student_management";  // Update this as needed
            String username = "root";  // Replace with your MySQL username
            String password = "databaseproject";  // Replace with your MySQL password
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(dbUrl, username, password);
            System.out.println("Connected to the database.");
        }
        return connection;

    } catch (SQLException e) {
        System.out.println("Error: " + e.getMessage());
    }
    return connection;
    }

}

 class ExecuteCode {

    private Connection connection;

    public ExecuteCode(Connection connection) {
        this.connection = connection;
    }

    public ArrayList<String> executeTerminalCode(String sqlQuery) {
        ArrayList<String> results = new ArrayList<>();

        try (Statement stmt = connection.createStatement()) {
            // Execute the query (can be SELECT, INSERT, UPDATE, DELETE)
            if (sqlQuery.trim().toUpperCase().startsWith("SELECT")) {
                ResultSet rs = stmt.executeQuery(sqlQuery);
                while (rs.next()) {
                    // Assuming the result set has a column, handle accordingly
                    String result = "";
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        result += metaData.getColumnLabel(i) + ": " + rs.getString(i) + " | ";
                    }
                    results.add(result);
                }
            } else {
                int affectedRows = stmt.executeUpdate(sqlQuery);
                results.add("Rows affected: " + affectedRows);
            }
        } catch (SQLException e) {
            results.add("Error executing query: " + e.getMessage());
        }

        return results;
    }
}

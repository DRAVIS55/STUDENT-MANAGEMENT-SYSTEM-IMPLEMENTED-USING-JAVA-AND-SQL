import java.sql.*;
import java.util.ArrayList;

public class Lecturer extends Person {
    private Connection connection;

    // Constructor to initialize database connection
    public Lecturer(Connection connection) {
        this.connection = connection;
    }

    // Register a new Lecturer
    public boolean registerLecturer(String lecturerID, String f_name, String s_name, String phone, String department, String email) {
        String sql = "INSERT INTO Lecturer (lectureIdNo, first_name, last_name, phone, department, email) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, lecturerID);
            stmt.setString(2, f_name);
            stmt.setString(3, s_name);
            stmt.setString(4, phone);
            stmt.setString(5, department);
            stmt.setString(6, email);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update Lecturer Details
    public boolean updateLecturer(String lecturerID, String newName, String newEmail) {
        String sql = "UPDATE Lecturer SET first_name = ?, email = ? WHERE lectureIdNo = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setString(2, newEmail);
            stmt.setString(3, lecturerID);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Assign courses to a lecturer
    public boolean assignCourses(String lecturerIdNo, ArrayList<String> courseCodes) {
        String sqlLecturerCheck = "SELECT lecturer_id FROM Lecturer WHERE lectureIdNo = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlLecturerCheck)) {
            stmt.setString(1, lecturerIdNo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String lecture_id=rs.getString("lecturer_id");
                // Lecturer found, now assign courses
                String sqlAssign = "INSERT INTO Lecturer_Courses (lecturer_id, course_id) VALUES (?, ?)";
                try (PreparedStatement assignStmt = connection.prepareStatement(sqlAssign)) {
                    for (String courseCode : courseCodes) {
                        // Assuming courseCode is valid and corresponds to course_id
                        assignStmt.setString(1, lecture_id);
                        assignStmt.setString(2, courseCode); // Assuming courseCode is the ID
                        assignStmt.addBatch();
                    }
                    assignStmt.executeBatch();
                    return true;
                }
            } else {
                System.out.println("Lecturer with ID " + lecturerIdNo + " does not exist.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Search for a Lecturer by ID
    public ArrayList<String> searchLecturer(String lecturerID) {
        ArrayList<String> lecturerDetails = new ArrayList<>();
        String sql = "SELECT * FROM Lecturer WHERE lectureIdNo = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, lecturerID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                lecturerDetails.add("Lecturer ID: " + rs.getString("lecturer_id"));
                lecturerDetails.add("First Name: " + rs.getString("first_name"));
                lecturerDetails.add("Last Name: " + rs.getString("last_name"));
                lecturerDetails.add("Phone: " + rs.getString("phone"));
                lecturerDetails.add("Department: " + rs.getString("department"));
                lecturerDetails.add("Email: " + rs.getString("email"));
            } else {
                lecturerDetails.add("Lecturer not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lecturerDetails;
    }
}

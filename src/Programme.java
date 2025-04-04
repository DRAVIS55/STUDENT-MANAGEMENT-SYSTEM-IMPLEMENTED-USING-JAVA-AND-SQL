import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;

public class Programme {
    private Connection connection;

    // Constructor to initialize database connection
    public Programme(Connection connection) {
        this.connection = connection;
    }

    // Add a programme to the database, ensuring no duplicates are inserted
    public boolean addProgram(String programmeName, String programmeCode, BigDecimal programmeCost) {
        String checkSQL = "SELECT COUNT(*) FROM Programme WHERE programme_code = ?";
        
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSQL)) {
            checkStmt.setString(1, programmeCode);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Create a new ReusableClass instance and print the message when needed
                    new ReusableClass().printMessage("❌ Programme code '" + programmeCode + "' already exists!");
                    return false;
                }
            }

            // Insert new programme if not duplicate
            String insertSQL = "INSERT INTO Programme (programme_code, programme_name, programme_cost) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
                stmt.setString(1, programmeCode);  // Use programme_code as primary key
                stmt.setString(2, programmeName);
                stmt.setBigDecimal(3, programmeCost);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    new ReusableClass().printMessage("✅ Programme '" + programmeName + "' with code '" + programmeCode + "' added successfully.");
                    return true;
                } else {
                    new ReusableClass().printMessage("❌ Programme addition failed. No rows were affected.");
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {  // MySQL error code for duplicate entry
                new ReusableClass().printMessage("❌ Duplicate entry for programme code '" + programmeCode + "'");
            } else {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Modify programme details in the database
    public boolean modifyProgram(BigDecimal cost, String programmeCode) {
        String sql = "UPDATE Programme SET programme_cost = ? WHERE programme_code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, cost);
            stmt.setString(2, programmeCode);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                new ReusableClass().printMessage("✅ Programme with code '" + programmeCode + "' updated successfully.");
                return true;
            } else {
                new ReusableClass().printMessage("❌ No changes made. Programme with code '" + programmeCode + "' not found or cost already the same.");
            }
        } catch (SQLException e) {
            new ReusableClass().printMessage("❌ Error while modifying programme with code: " + programmeCode);
            e.printStackTrace();
        }
        return false;
    }

    // Remove a programme from the database
    public boolean removeProgram(String programmeCode) {
        String sql = "DELETE FROM Programme WHERE programme_code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, programmeCode);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                new ReusableClass().printMessage("✅ Programme with code '" + programmeCode + "' removed successfully.");
                return true;
            } else {
                new ReusableClass().printMessage("❌ Programme with code '" + programmeCode + "' not found or already removed.");
            }
        } catch (SQLException e) {
            new ReusableClass().printMessage("❌ Error while removing programme with code: " + programmeCode);
            e.printStackTrace();
        }
        return false;
    }

    // Attach courses to a programme in the database
    public boolean attachProgramCourses(String programmeCode, String courseCode) {
        String sql = "INSERT INTO Programme_Courses (programme_code, course_code) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, programmeCode);
            stmt.setString(2, courseCode);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                new ReusableClass().printMessage("✅ Course '" + courseCode + "' successfully attached to programme with code '" + programmeCode + "'.");
                return true;
            } else {
                new ReusableClass().printMessage("❌ Failed to attach course '" + courseCode + "' to programme '" + programmeCode + "'.");
            }
        } catch (SQLException e) {
            new ReusableClass().printMessage("❌ Error while attaching course to programme: " + programmeCode);
            e.printStackTrace();
        }
        return false;
    }

    // Search for students by programme
    // Inside Programme.java
public ArrayList<String> searchStudentsByProgramme(String programmeCode) {
    ArrayList<String> students = new ArrayList<>();
    String sql = "SELECT s.student_id, s.first_name, s.last_name FROM Students s " +
                 "JOIN Programmes p ON s.programme_code = p.programme_code " +
                 "WHERE p.programme_code = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, programmeCode);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            students.add(rs.getString("student_id") + " - " + rs.getString("first_name") + " " + rs.getString("last_name"));
        }
    } catch (SQLException e) {
        new ReusableClass().printMessage("❌ Error while searching students for programme: " + programmeCode);
        e.printStackTrace();
    }

    return students;
}

   // Assign courses to a lecturer
   public boolean assignCourses(String lecturerIdNo, ArrayList<String> courseCodes) {
    String sqlLecturerCheck = "SELECT lecturer_id FROM Lecturer WHERE lecturer_id_no = ?";  // Ensure correct column name
    try (PreparedStatement stmt = connection.prepareStatement(sqlLecturerCheck)) {
        stmt.setString(1, lecturerIdNo);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String lecturer_id = rs.getString("lecturer_id");

            // Validate if courses exist
            ArrayList<String> validCourses = new ArrayList<>();
            String sqlCourseCheck = "SELECT course_id FROM Course WHERE course_id = ?";
            try (PreparedStatement courseStmt = connection.prepareStatement(sqlCourseCheck)) {
                for (String courseCode : courseCodes) {
                    courseStmt.setString(1, courseCode);
                    ResultSet courseRs = courseStmt.executeQuery();
                    if (courseRs.next()) {
                        validCourses.add(courseCode);
                    } else {
                        System.out.println("⚠️ Course ID '" + courseCode + "' does not exist. Skipping...");
                    }
                }
            }

            // If no valid courses, return false
            if (validCourses.isEmpty()) {
                System.out.println("❌ No valid courses found for assignment.");
                return false;
            }

            // Assign courses
            String sqlAssign = "INSERT INTO Lecturer_Courses (lecturer_id, course_id) VALUES (?, ?)";
            try (PreparedStatement assignStmt = connection.prepareStatement(sqlAssign)) {
                int assigned = 0;
                for (String validCourse : validCourses) {
                    assignStmt.setString(1, lecturer_id);
                    assignStmt.setString(2, validCourse);
                    assignStmt.addBatch();
                    assigned++;
                }
                assignStmt.executeBatch();
                System.out.println("✅ Successfully assigned " + assigned + " course(s) to Lecturer " + lecturerIdNo);
                return true;
            }
        } else {
            System.out.println("❌ Lecturer with ID " + lecturerIdNo + " does not exist.");
            return false;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
}

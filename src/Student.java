import java.sql.*;
import java.util.ArrayList;

public class Student extends Person {

    private Connection connection;

    // Constructor
    public Student(Connection connection) {
        this.connection = connection;
    }

    // Register a student in the database
    public void registerStudent(String firstName, String lastName, String email, String phone, String gender, String programmeCode) {
        String checkProgrammeSQL = "SELECT COUNT(*) FROM Programme WHERE programme_code = ?";
        String insertStudentSQL = "INSERT INTO Student (first_name, last_name, email, phone, gender, programme_code) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement checkStmt = connection.prepareStatement(checkProgrammeSQL)) {
            checkStmt.setString(1, programmeCode);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                new ReusableClass().printMessage("❌ Error: Programme ID '" + programmeCode + "' does not exist!");
                return;
            }

            try (PreparedStatement insertStmt = connection.prepareStatement(insertStudentSQL)) {
                insertStmt.setString(1, firstName);
                insertStmt.setString(2, lastName);
                insertStmt.setString(3, email);
                insertStmt.setString(4, phone);
                insertStmt.setString(5, gender);
                insertStmt.setString(6, programmeCode);
                int rowsAffected = insertStmt.executeUpdate();
                if (rowsAffected > 0) {
                    new ReusableClass().printMessage("✅ Student registered successfully!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new ReusableClass().printMessage("❌ Error registering student!");
        }
    }

    // Enroll a student in a course
    public void enrollStudent(String studentID, String courseCode, String semester, String score) {
        String checkStudentSQL = "SELECT COUNT(*) FROM Student WHERE student_id = ?";
        String checkCourseSQL = "SELECT COUNT(*) FROM Course WHERE course_code = ?";
        String enrollSQL = "INSERT INTO Student_Courses (student_id, course_code, semester, score) VALUES (?, ?, ?, ?)";

        try (PreparedStatement checkStudentStmt = connection.prepareStatement(checkStudentSQL);
             PreparedStatement checkCourseStmt = connection.prepareStatement(checkCourseSQL)) {

            // Check if student exists
            checkStudentStmt.setString(1, studentID);
            ResultSet studentResult = checkStudentStmt.executeQuery();
            studentResult.next();
            if (studentResult.getInt(1) == 0) {
                new ReusableClass().printMessage("❌ Student not found!");
                return;
            }

            // Check if course exists
            checkCourseStmt.setString(1, courseCode);
            ResultSet courseResult = checkCourseStmt.executeQuery();
            courseResult.next();
            if (courseResult.getInt(1) == 0) {
                new ReusableClass().printMessage("❌ Course not found!");
                return;
            }

            // Enroll student in the course
            try (PreparedStatement enrollStmt = connection.prepareStatement(enrollSQL)) {
                enrollStmt.setString(1, studentID);
                enrollStmt.setString(2, courseCode);
                enrollStmt.setString(3, semester);
                enrollStmt.setString(4, score);
                int rowsAffected = enrollStmt.executeUpdate();
                if (rowsAffected > 0) {
                    new ReusableClass().printMessage("✅ Student enrolled successfully in the course!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new ReusableClass().printMessage("❌ Error enrolling student to course!");
        }
    }

    // Assign scores to a student for a specific course
    public void assignScores(String studentID, String courseCode, String score) {
        String updateScoreSQL = "UPDATE Student_Courses SET score = ? WHERE student_id = ? AND course_code = ?";

        try (PreparedStatement stmt = connection.prepareStatement(updateScoreSQL)) {
            stmt.setString(1, score);
            stmt.setString(2, studentID);
            stmt.setString(3, courseCode);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                new ReusableClass().printMessage("✅ Score successfully assigned!");
            } else {
                new ReusableClass().printMessage("❌ Student-course pair not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new ReusableClass().printMessage("❌ Error assigning score!");
        }
    }

    // Search for a student by ID and return details
    public ArrayList<String> searchStudent(String studentID) {
        ArrayList<String> details = new ArrayList<>();
        String sql = "SELECT student_id, first_name, last_name, email, phone, gender, programme_code FROM Student WHERE student_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                details.add("Student ID: " + rs.getString("student_id"));
                details.add("First Name: " + rs.getString("first_name"));
                details.add("Last Name: " + rs.getString("last_name"));
                details.add("Email: " + rs.getString("email"));
                details.add("Phone: " + rs.getString("phone"));
                details.add("Gender: " + rs.getString("gender"));
                details.add("Programme Code: " + rs.getString("programme_code"));
            } else {
                details.add("❌ Student not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            details.add("❌ Error searching student!");
        }
        return details;
    }

    // Search student results by student ID
    public ArrayList<String> searchStudentResults(String studentId) {
        ArrayList<String> results = new ArrayList<>();
        String sql = "SELECT sc.course_code, sc.score, c.course_name FROM Student_Courses sc " +
                     "JOIN Course c ON sc.course_code = c.course_code WHERE sc.student_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String courseName = rs.getString("course_name");
                String score = rs.getString("score");
                results.add(courseName + " - Score: " + score);
            }

            if (results.isEmpty()) {
                results.add("No courses found for this student.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            results.add("❌ Error fetching student results!");
        }
        return results;
    }
}

import java.sql.*;
import java.util.ArrayList;

public class Course {
    private Connection connection;

    // Constructor to initialize database connection
    public Course(Connection connection) {
        this.connection = connection;
    }

    // Add a new course
    public void addCourse(String courseCode, String name,String creditHours) {
        String sql = "INSERT INTO Course (course_code, course_name, credit_hours) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Set the values for the placeholders in the query
            stmt.setString(1, courseCode);
            stmt.setString(2, name);
            stmt.setString(3, creditHours);

            // Execute the query
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                new ReusableClass().printMessage("Course registered successfully!");
            } else {
                new ReusableClass().printMessage("Failed to register the course!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new ReusableClass().printMessage("App error: Unable to add course! " + e.getMessage());
        }
    }

    // Modify an existing course
    public void modifyCourse(String courseCode, String creditHours) {
        String sql = "UPDATE Course SET credit_hours = ? WHERE course_code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, creditHours);
            stmt.setString(2, courseCode);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                new ReusableClass().printMessage("Course modified successfully!");
            } else {
                new ReusableClass().printMessage("Course not found or modification failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new ReusableClass().printMessage("App error: Unable to modify course! " + e.getMessage());
        }
    }

    // Delete an existing course
    public void deleteCourse(String courseCode) {
        String sql = "DELETE FROM Course WHERE course_code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, courseCode);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                new ReusableClass().printMessage("Course deleted successfully!");
            } else {
                new ReusableClass().printMessage("Course not found or deletion failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new ReusableClass().printMessage("App error: Unable to delete course! " + e.getMessage());
        }
    }

    // Show all courses
    public ArrayList<String> showCourses() {
        ArrayList<String> contentList = new ArrayList<>();
        String sql = "SELECT * FROM Course";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String courseInfo = rs.getString("course_code") + " - " + rs.getString("course_name") + " | Credits: " + rs.getInt("credit_hours");
                contentList.add(courseInfo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new ReusableClass().printMessage("App Error: Unable to load courses!");
        }
        return contentList;
    }

    // Search for students registered in a specific course
    public ArrayList<String> searchRegisteredStudents(String courseCode) {
        ArrayList<String> students = new ArrayList<>();
        String sql = "SELECT s.student_id, s.first_name, s.last_name " +
                     "FROM Student s " +
                     "JOIN student_courses sc ON s.student_id = sc.student_id " +
                     "JOIN Course c ON sc.course_code = c.course_code " +
                     "WHERE c.course_code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, courseCode);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean studentsFound = false;
                while (rs.next()) {
                    studentsFound = true;
                    students.add("Student ID: " + rs.getInt("student_id") +
                                 " - Name: " + rs.getString("first_name") + " " + rs.getString("last_name"));
                }

                if (!studentsFound) {
                    students.add("No students found for this course.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            students.add("Error retrieving registered students.");
        }
        return students;
    }

    // Get lecturers assigned to a specific course
    public ArrayList<String[]> getLecturersByCourse(String courseCode) {
        ArrayList<String[]> lecturers = new ArrayList<>();
        String sql = "SELECT l.lecturer_id, l.first_name, l.last_name " +
                     "FROM Lecturer l " +
                     "JOIN Lecturer_Courses lc ON l.lecturer_id = lc.lecturer_id " +
                     "JOIN Course c ON lc.course_code = c.course_code " +
                     "WHERE c.course_code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, courseCode);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean lecturersFound = false;
                while (rs.next()) {
                    lecturersFound = true;
                    lecturers.add(new String[]{rs.getString("lecturer_id"),
                                                rs.getString("first_name"),
                                                rs.getString("last_name")});
                }

                if (!lecturersFound) {
                    lecturers.add(new String[]{"No lecturers found for this course."});
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            lecturers.add(new String[]{"Error retrieving lecturers."});
        }
        return lecturers;
    }
}

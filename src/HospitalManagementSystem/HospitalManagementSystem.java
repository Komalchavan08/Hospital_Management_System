package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hospital";

    private static final String username = "root";

    // Note: In production, passwords should be hashed (e.g., BCrypt)
    private static final String password = "root";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            if (!adminLogin(connection, scanner)) {
                System.out.println("Invalid credentials. Exiting...");
                System.exit(0);
            }

            while (true) {
                System.out.println("Hospital Management System");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. Delete Patient");
                System.out.println("4. View Doctors");
                System.out.println("5. Book Appointment");
                System.out.println("6. View Appointments");
                System.out.println("7. Cancel Appointment");
                System.out.println("8. Exit");

                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        patient.deletePatient(connection, scanner);
                        break;
                    case 4:
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 5:
                        bookAppointment(patient, doctor, connection, scanner);
                        System.out.println();
                        break;
                    case 6:
                        viewAppointments(connection);
                        break;
                    case 7:
                        cancelAppointment(connection, scanner);
                        break;
                    case 8:
                        System.out.println("THANK YOU! FOR USING HOSPITAL MANAGEMENT SYSTEM!!");
                        return;
                    default:
                        System.out.println("Enter valid choice!!");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean adminLogin(Connection connection, Scanner scanner) {
        System.out.println("===== Admin Login =====");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            String sql = "SELECT * FROM admin_users WHERE username=? AND password=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\nLogin Successful!\n");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error checking login: " + e.getMessage());
        }
        return false;
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.print("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();
        scanner.nextLine();

        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
            if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES (?,?,?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Appointment Booked!!");
                    } else {
                        System.out.println("Failed to book appointment.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Doctor not available on this date!!");
            }
        } else {
            System.out.println("Either doctor or patient doesn't exist!!");
        }
    }

    public static void viewAppointments(Connection connection) {
        System.out.println("\nAppointments: ");
        System.out.println("+------------+------------------+-----------------+------------------+");
        System.out.println("| Appt.ID    | Patient Name     | Doctor Name     | Date             |");
        System.out.println("+------------+------------------+-----------------+------------------+");

        String query = """
                SELECT a.id AS appt_id,
                       p.name AS patient_name,
                       d.name AS doctor_name,
                       a.appointment_date
                FROM appointments a
                JOIN patients p ON a.patient_id = p.id
                JOIN doctors d ON a.doctor_id = d.id
                ORDER BY a.appointment_date;
                """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("| %-10s | %-16s | %-15s | %-16s |\n",
                        rs.getInt("appt_id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getDate("appointment_date"));
            }
            if (!found) {
                System.out.println("| No appointments found.                                              |");
            }
            System.out.println("+------------+------------------+-----------------+------------------+");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void cancelAppointment(Connection connection, Scanner scanner) {
        System.out.print("Enter Appointment ID to cancel: ");
        int appointmentId = scanner.nextInt();
        scanner.nextLine();

        String checkQuery = "SELECT * FROM appointments WHERE id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(checkQuery);
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                System.out.println("Appointment not found!");
                return;
            }
        } catch (SQLException e) {
            System.out.println("Error checking appointment: " + e.getMessage());
            return;
        }

        System.out.print("Are you sure you want to cancel this appointment? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (!confirmation.equals("yes")) {
            System.out.println("Cancellation aborted.");
            return;
        }

        String deleteQuery = "DELETE FROM appointments WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(deleteQuery);
            ps.setInt(1, appointmentId);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Appointment cancelled successfully!!");
            } else {
                System.out.println("Failed to cancel appointment.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
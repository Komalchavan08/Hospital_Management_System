package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;
import java.util.Scanner;

public class Patient {

    private Connection connection;
    private Scanner scanner;

    public Patient(Connection connection, Scanner scanner)
    {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void addPatient(){
        System.out.print("Enter Patient Name: ");
        String name=scanner.next();
        System.out.print("Enter Patient Age: ");
        int age=scanner.nextInt();
        System.out.print("Enter Patient Gender: ");
        String gender=scanner.next();

        try {
            String query="INSERT INTO patients(name, age, gender) VALUES(?,?,?)";
            PreparedStatement preparedStatement= connection.prepareStatement(query);
            preparedStatement.setString(1,name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3,gender);
            int affectedRows=preparedStatement.executeUpdate();
            if(affectedRows>0){
                System.out.println("Patient added successfully!!");
            }else {
                System.out.println("Failed to add patient!!");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void viewPatients(){
        String query = "select * from patients";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet=preparedStatement.executeQuery();
            System.out.println("Patients: ");
            System.out.println("+------------+--------------------+---------+--------------+");
            System.out.println("| Patient Id | Name               | Age     | Gender       |");
            System.out.println("+------------+--------------------+---------+--------------+");
            while (resultSet.next()){
                int id= resultSet.getInt("id");
                String name =resultSet.getString("name");
                int age =resultSet.getInt("age");
                String gender =resultSet.getString("gender");
                System.out.printf("| %-10s | %-18s | %-8s | %-11s |\n", id, name, age, gender);
                System.out.println("+------------+--------------------+------- -+--------------+");

            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public boolean getPatientById(int id){
        String query = "SELECT * FROM patients WHERE id = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }else {
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public void deletePatient(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter Patient ID to delete: ");
            int id = scanner.nextInt();
            scanner.nextLine();

            // Confirm deletion
            System.out.print("Are you sure you want to delete this record? (yes/no): ");
            String confirm = scanner.nextLine();

            if (!confirm.equalsIgnoreCase("yes")) {
                System.out.println("❌ Deletion cancelled.");
                return;
            }

            String deleteQuery = "DELETE FROM patients WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(deleteQuery);
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("🗑️ Patient deleted successfully!");
            } else {
                System.out.println("⚠️ No patient found with that ID.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting record: " + e.getMessage());
        }
    }


}

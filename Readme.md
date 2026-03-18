# 🏥 Hospital Management System

A console-based Java application for managing hospital operations — patients, doctors, and appointments — with real MySQL database integration using JDBC.

---

## ✨ Features

- 🔐 **Admin Login** — Secure login system before accessing any operations
- 👤 **Patient Management** — Add, view and delete patient records
- 👨‍⚕️ **Doctor Management** — View all available doctors and specializations
- 📅 **Appointment Booking** — Book appointments with doctor availability check
- 📋 **View Appointments** — See all appointments with patient & doctor details using SQL JOINs
- ❌ **Cancel Appointment** — Cancel with confirmation prompt
- ✅ **Input Validation** — Age validation, confirmation before delete/cancel
- 🛡️ **SQL Injection Safe** — All queries use PreparedStatements

---

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Java 17 | Core programming language |
| JDBC | Database connectivity |
| MySQL | Relational database |
| PreparedStatement | Safe SQL query execution |
| OOP (Classes) | Patient, Doctor, HospitalManagementSystem |

---

## 📁 Project Structure

```
HospitalManagementSystem/
├── HospitalManagementSystem.java   # Main class — menu, appointments, admin login
├── Patient.java                    # Patient CRUD operations
├── Doctor.java                     # Doctor view & lookup
└── README.md
```

---

## 🗄️ Database Setup

Run the following SQL in MySQL to set up the database:

```sql
CREATE DATABASE hospital;
USE hospital;

CREATE TABLE patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(10) NOT NULL
);

CREATE TABLE doctors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL
);

CREATE TABLE appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT,
    doctor_id INT,
    appointment_date DATE,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

CREATE TABLE admin_users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL
);

-- Insert default admin credentials
INSERT INTO admin_users (username, password) VALUES ('admin', 'admin123');

-- Insert sample doctors
INSERT INTO doctors (name, specialization) VALUES
('Dr. Sharma', 'Cardiologist'),
('Dr. Mehta', 'Neurologist'),
('Dr. Patil', 'Orthopedic'),
('Dr. Rao', 'Dermatologist');
```

---

## ▶️ How to Run

1. **Clone the repository**
```bash
git clone https://github.com/Komalchavan08/Hospital_Management_System.git
```

2. **Set up MySQL** — Run the SQL script above

3. **Add MySQL JDBC Driver** to your project:
    - Download `mysql-connector-j-x.x.x.jar` from [MySQL Downloads](https://dev.mysql.com/downloads/connector/j/)
    - Add it to your project classpath in IntelliJ IDEA:
      `File → Project Structure → Libraries → + → Select the JAR`

4. **Update credentials** in `HospitalManagementSystem.java` if needed:
```java
private static final String url = "jdbc:mysql://localhost:3306/hospital";
private static final String username = "root";
private static final String password = "your_password";
```

5. **Run** `HospitalManagementSystem.java`

6. **Login** with default credentials:
    - Username: `admin`
    - Password: `admin123`

---

## 📸 Sample Output

```
===== Admin Login =====
Enter username: admin
Enter password: admin123

Login Successful!

Hospital Management System
1. Add Patient
2. View Patients
3. Delete Patient
4. View Doctors
5. Book Appointment
6. View Appointments
7. Cancel Appointment
8. Exit

+------------+--------------------+---------+--------------+
| Patient Id | Name               | Age     | Gender       |
+------------+--------------------+---------+--------------+
| 1          | Ravi Kumar         | 35      | Male         |
+------------+--------------------+---------+--------------+

+------------+-------------------+-------------------------+
| Doctor Id  | Name              | Specialization          |
+------------+-------------------+-------------------------+
| 1          | Dr. Sharma        | Cardiologist            |
+------------+-------------------+-------------------------+
```

---

## 🏗️ Architecture

```
User Input (Console)
        ↓
HospitalManagementSystem.java   ← Main menu & appointment logic
        ↓
Patient.java / Doctor.java      ← Business operations per entity
        ↓
JDBC PreparedStatement          ← Safe DB queries
        ↓
MySQL Database                  ← patients, doctors, appointments tables
```

---

## 👩‍💻 Author

**Komal Chavan**
- 🎓 MCA Graduate — Shivaji University, Kolhapur
- 💼 GitHub: [@Komalchavan08](https://github.com/Komalchavan08)
- 🔗 LinkedIn: [komal-chavan](https://www.linkedin.com/in/komal-chavan-498a06266)

---

## 📌 Note

> Built as an academic project to demonstrate Java + JDBC + MySQL integration with proper OOP design. All database operations use PreparedStatements to prevent SQL injection.
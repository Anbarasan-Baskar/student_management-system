# Student Management System

A Java Swing-based Student Management System with full CRUD operations, search functionality, and an admin login. The project connects to a MySQL database using JDBC and features a modern UI with gradient backgrounds and responsive design.

---

## Features

- **Admin Login:** Secure login for admin access.
- **Add Student:** Insert new student records into the database.
- **Update Student:** Edit existing student details.
- **Delete Student:** Remove student records.
- **Search Student:** Search for a student by Student ID.
- **Responsive Design:** Modern UI with gradient backgrounds and styled tables.
- **Data Persistence:** All student records are stored in a MySQL database.

---

## Screenshots

**Login Page**  
![Login Page](screenshots/login_page.png)

**Main Dashboard**  
![Main Dashboard](screenshots/main_dashboard.png)

**Add Student**  
![Add Student](screenshots/add_student.png)

**Update Student**  
![Update Student](screenshots/update_student.png)

**Delete Student**  
![Delete Student](screenshots/delete_student.png)

**Search Student**  
![Search Student](screenshots/search_student.png)

---

## Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/anbarasanbaskar/student-management-system.git
   cd student-management-system


Set up MySQL database:

CREATE DATABASE student;
CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    student_id VARCHAR(255) UNIQUE,
    grade VARCHAR(10),
    date_of_birth DATE,
    gender VARCHAR(10),
    contact VARCHAR(20),
    email VARCHAR(255)
);



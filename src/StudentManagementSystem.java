import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentManagementSystem extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private Connection connection;

    public StudentManagementSystem() {
        setTitle("Student Management System");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Gradient background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(58, 123, 213),
                                                     0, getHeight(), new Color(58, 213, 178));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new BorderLayout(20, 20));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Heading
        JLabel heading = new JLabel("Student Management System", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 28));
        heading.setForeground(Color.WHITE);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        backgroundPanel.add(heading, BorderLayout.NORTH);

        // Center panel (white card)
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Table
        model = new DefaultTableModel(new String[]{
                "ID", "Name", "Student ID", "Grade", "DOB", "Gender", "Contact", "Email"}, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons + Search
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBackground(Color.WHITE);

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");
        JTextField searchField = new JTextField(15);
        JButton searchBtn = new JButton("Search");

        for (JButton btn : new JButton[]{addBtn, updateBtn, deleteBtn, refreshBtn, searchBtn}) {
            btn.setBackground(new Color(58, 123, 213));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        }

        controlPanel.add(addBtn);
        controlPanel.add(updateBtn);
        controlPanel.add(deleteBtn);
        controlPanel.add(refreshBtn);
        controlPanel.add(searchField);
        controlPanel.add(searchBtn);

        centerPanel.add(controlPanel, BorderLayout.SOUTH);
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
        add(backgroundPanel);

        // DB
        connectToDatabase();
        loadStudents();

        // Button actions
        addBtn.addActionListener(e -> openStudentForm("Add", null));
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) openStudentForm("Update", row);
            else JOptionPane.showMessageDialog(this, "Select a student to update!");
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) deleteStudent(row);
            else JOptionPane.showMessageDialog(this, "Select a student to delete!");
        });
        refreshBtn.addActionListener(e -> loadStudents());
        searchBtn.addActionListener(e -> searchStudents(searchField.getText()));
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/student";
            String user = "root";
            String password = "1234"; // change to your password
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
            System.exit(1);
        }
    }

    private void loadStudents() {
        try {
            model.setRowCount(0);
            String query = "SELECT * FROM students";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("student_id"),
                        rs.getString("grade"),
                        rs.getDate("date_of_birth"),
                        rs.getString("gender"),
                        rs.getString("contact"),
                        rs.getString("email")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openStudentForm(String action, Integer row) {
        JDialog dialog = new JDialog(this, action + " Student", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(8, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField studentIdField = new JTextField();
        JTextField gradeField = new JTextField();
        JTextField dobField = new JTextField();
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        JTextField contactField = new JTextField();
        JTextField emailField = new JTextField();

        dialog.add(new JLabel("Name:")); dialog.add(nameField);
        dialog.add(new JLabel("Student ID:")); dialog.add(studentIdField);
        dialog.add(new JLabel("Grade:")); dialog.add(gradeField);
        dialog.add(new JLabel("DOB (yyyy-mm-dd):")); dialog.add(dobField);
        dialog.add(new JLabel("Gender:")); dialog.add(genderBox);
        dialog.add(new JLabel("Contact:")); dialog.add(contactField);
        dialog.add(new JLabel("Email:")); dialog.add(emailField);

        JButton saveBtn = new JButton(action);
        dialog.add(new JLabel()); dialog.add(saveBtn);

        if (row != null) { // update prefill
            nameField.setText((String) model.getValueAt(row, 1));
            studentIdField.setText((String) model.getValueAt(row, 2));
            gradeField.setText((String) model.getValueAt(row, 3));
            dobField.setText(model.getValueAt(row, 4).toString());
            genderBox.setSelectedItem(model.getValueAt(row, 5));
            contactField.setText((String) model.getValueAt(row, 6));
            emailField.setText((String) model.getValueAt(row, 7));
        }

        saveBtn.addActionListener(e -> {
            String name = nameField.getText();
            String studentId = studentIdField.getText();
            String grade = gradeField.getText();
            String dob = dobField.getText();
            String gender = (String) genderBox.getSelectedItem();
            String contact = contactField.getText();
            String email = emailField.getText();

            try {
                if (action.equals("Add")) {
                    String sql = "INSERT INTO students (name, student_id, grade, date_of_birth, gender, contact, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ps.setString(1, name); ps.setString(2, studentId); ps.setString(3, grade);
                    ps.setString(4, dob); ps.setString(5, gender); ps.setString(6, contact); ps.setString(7, email);
                    ps.executeUpdate();
                } else {
                    String sql = "UPDATE students SET name=?, grade=?, date_of_birth=?, gender=?, contact=?, email=? WHERE student_id=?";
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ps.setString(1, name); ps.setString(2, grade); ps.setString(3, dob);
                    ps.setString(4, gender); ps.setString(5, contact); ps.setString(6, email);
                    ps.setString(7, studentId);
                    ps.executeUpdate();
                }
                loadStudents();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    private void deleteStudent(int row) {
        String studentId = (String) model.getValueAt(row, 2);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete student ID " + studentId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM students WHERE student_id=?";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, studentId);
                ps.executeUpdate();
                loadStudents();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void searchStudents(String keyword) {
        try {
            model.setRowCount(0);
            String sql = "SELECT * FROM students WHERE name LIKE ? OR student_id LIKE ? OR grade LIKE ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("student_id"),
                        rs.getString("grade"),
                        rs.getDate("date_of_birth"),
                        rs.getString("gender"),
                        rs.getString("contact"),
                        rs.getString("email")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------- LOGIN PAGE ----------------
    public static class LoginPage extends JFrame {
        public LoginPage() {
            setTitle("Admin Login");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel bg = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    GradientPaint gp = new GradientPaint(0, 0, new Color(58, 123, 213),
                                                         0, getHeight(), new Color(58, 213, 178));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            bg.setLayout(new BorderLayout());

            JLabel heading = new JLabel("Student Management", SwingConstants.CENTER);
            heading.setFont(new Font("Arial", Font.BOLD, 22));
            heading.setForeground(Color.WHITE);
            heading.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
            bg.add(heading, BorderLayout.NORTH);

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10); gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel userLabel = new JLabel("Username:"); JTextField userField = new JTextField(15);
            JLabel passLabel = new JLabel("Password:"); JPasswordField passField = new JPasswordField(15);
            JButton loginBtn = new JButton("Login");

            gbc.gridx = 0; gbc.gridy = 0; panel.add(userLabel, gbc);
            gbc.gridx = 1; panel.add(userField, gbc);
            gbc.gridx = 0; gbc.gridy = 1; panel.add(passLabel, gbc);
            gbc.gridx = 1; panel.add(passField, gbc);
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; panel.add(loginBtn, gbc);

            loginBtn.setBackground(new Color(58, 123, 213)); loginBtn.setForeground(Color.WHITE); loginBtn.setFont(new Font("Arial", Font.BOLD, 14));

            bg.add(panel, BorderLayout.CENTER);
            add(bg);

            loginBtn.addActionListener(e -> {
                if (userField.getText().equals("admin") && new String(passField.getPassword()).equals("admin")) {
                    new StudentManagementSystem().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid login!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}

package com.example;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class EmployeeServlet extends HttpServlet {

    // JDBC configuration
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/employee_db";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "saylee@2005";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String empId = request.getParameter("empId");
        String name = request.getParameter("name");
        String dept = request.getParameter("dept");
        String salary = request.getParameter("salary");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS)) {
            Class.forName("com.mysql.cj.jdbc.Driver");

            if ("Insert".equals(action)) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO employees VALUES (?, ?, ?, ?)");
                ps.setInt(1, Integer.parseInt(empId));
                ps.setString(2, name);
                ps.setString(3, dept);
                ps.setDouble(4, Double.parseDouble(salary));
                ps.executeUpdate();

            } else if ("Update".equals(action)) {
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE employees SET name=?, department=?, salary=? WHERE empid=?");
                ps.setString(1, name);
                ps.setString(2, dept);
                ps.setDouble(3, Double.parseDouble(salary));
                ps.setInt(4, Integer.parseInt(empId));
                ps.executeUpdate();

            } else if ("Delete".equals(action)) {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM employees WHERE empid=?");
                ps.setInt(1, Integer.parseInt(empId));
                ps.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("employees");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Employee Management</title>");
        out.println("<style>");
        out.println("table{border-collapse:collapse;width:70%;margin:auto;} th,td{border:1px solid #333;padding:8px;text-align:center;}");
        out.println("h2{text-align:center;}");
        out.println("form{text-align:center;margin-bottom:20px;}");
        out.println("</style></head><body>");
        out.println("<h2>Employee Management System</h2>");

        // Form for insert/update/delete
        out.println("<form method='post' action='employees'>");
        out.println("Employee ID: <input type='text' name='empId' required> ");
        out.println("Name: <input type='text' name='name'> ");
        out.println("Department: <input type='text' name='dept'> ");
        out.println("Salary: <input type='text' name='salary'> ");
        out.println("<br><br>");
        out.println("<input type='submit' name='action' value='Insert'> ");
        out.println("<input type='submit' name='action' value='Update'> ");
        out.println("<input type='submit' name='action' value='Delete'> ");
        out.println("</form>");

        // Display table
        out.println("<table><tr><th>ID</th><th>Name</th><th>Department</th><th>Salary</th></tr>");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM employees");

            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("empid") + "</td>");
                out.println("<td>" + rs.getString("name") + "</td>");
                out.println("<td>" + rs.getString("department") + "</td>");
                out.println("<td>" + rs.getDouble("salary") + "</td>");
                out.println("</tr>");
            }

            conn.close();
        } catch (Exception e) {
            out.println("<tr><td colspan='4'>Error: " + e.getMessage() + "</td></tr>");
        }

        out.println("</table></body></html>");
    }
}


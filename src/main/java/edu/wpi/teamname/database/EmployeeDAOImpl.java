package edu.wpi.teamname.database;

import edu.wpi.teamname.database.interfaces.LoginDAO;
import edu.wpi.teamname.employees.ClearanceLevel;
import edu.wpi.teamname.employees.Employee;
import edu.wpi.teamname.employees.EmployeeType;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAOImpl implements LoginDAO {
  /**
   * This method updates an existing Login object in the "Login" table in the database with the new
   * Login object.
   *
   * @param employee the new Login object to be updated in the "Login" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void sync(Employee employee) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query = "";
    PreparedStatement statement = null;
    try {
      // Updating data in employee
      query =
          "UPDATE \"Employee\" SET \"password\" = ?, \"username\" = ?, \"employeeID\" = ?, \"firstName\" = ?, \"lastName\" = ?, \"clearanceLevel\" = ?, \"type\" = ? WHERE \"username\" = ?";
      statement = connection.prepareStatement(query);
      statement.setString(1, employee.getPassword());
      statement.setString(2, employee.getUsername());
      statement.setInt(3, employee.getEmployeeID());
      statement.setString(4, employee.getFirstName());
      statement.setString(5, employee.getLastName());
      statement.setString(6, employee.getLevel().getString());
      statement.setString(7, employee.getType().getString());
      statement.setString(8, employee.getOriginalUsername());
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    // Adding new types
    /*for (EmployeeType type : employee.getType()) {
      connection = DataManager.DbConnection();
      query = "SELECT count(*) count FROM \"EmployeeType\" WHERE \"username\" = ? AND \"type\" = ?";
      statement = connection.prepareStatement(query);
      statement.setString(1, employee.getUsername());
      statement.setString(2, type.getString());
      ResultSet rs = statement.executeQuery();
      int num = 0;
      while (rs.next()) {
        num = rs.getInt("count");
      }
      if (num == 0) {
        query = "INSERT INTO \"EmployeeType\" (username, type) VALUES (?, ?)";
        statement = connection.prepareStatement(query);
        statement.setString(1, employee.getUsername());
        statement.setString(2, type.getString());
        statement.executeUpdate();
      }
    }

    // Deleting old types
    connection = DataManager.DbConnection();
    query = "SELECT type FROM \"EmployeeType\" WHERE username = ?";
    statement = connection.prepareStatement(query);
    statement.setString(1, employee.getUsername());
    ResultSet rs = statement.executeQuery();
    while (rs.next()) {
      EmployeeType dbEmployeeType = EmployeeType.valueOf(rs.getString("type"));
      if (!employee.getType().contains(dbEmployeeType)) {
        query = "DELETE FROM \"EmployeeType\" WHERE username = ? AND type = ?";
        connection = DataManager.DbConnection();
        statement = connection.prepareStatement(query);
        statement.setString(1, employee.getUsername());
        statement.setString(2, dbEmployeeType.getString());
        statement.executeUpdate();
      }
    }*/
    connection.close();
  }

  /**
   * This method retrieves the types of an Employee object with the specified username from the
   * "EmployeeType" table in the database.
   *
   * @param username the username of the Employee object to retrieve types from the "EmployeeType"
   *     table
   * @return a list of EmployeeType objects of the Employee with the specified username
   * @throws SQLException if there is a problem accessing the database
   */
  /*public ArrayList<EmployeeType> getEmployeeType(String username) throws SQLException {
    Connection connection = DataManager.DbConnection();
    ArrayList<EmployeeType> employeeTypes = new ArrayList<>();
    try {
      String query = "SELECT * FROM \"EmployeeType\" WHERE \"username\" = ?";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setString(1, username);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        EmployeeType employeeType = EmployeeType.valueOf(rs.getString("type"));
        employeeTypes.add(employeeType);
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return employeeTypes;
  }*/

  /**
   * The method retrieves all the Employee objects from the "Employee" table in the database.
   *
   * @return an ArrayList of the Employee objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public ArrayList<Employee> getAll() throws SQLException {
    Connection connection = DataManager.DbConnection();
    ArrayList<Employee> list = new ArrayList<Employee>();
    try {
      String query = "SELECT * FROM \"Employee\"";
      PreparedStatement statement = connection.prepareStatement(query);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        String usern = rs.getString("username");
        String passw = rs.getString("password");
        int id = rs.getInt("employeeID");
        String fname = rs.getString("firstName");
        String lname = rs.getString("lastName");
        EmployeeType type = EmployeeType.valueOf(rs.getString("type"));
        ClearanceLevel level = ClearanceLevel.valueOf(rs.getString("clearanceLevel"));
        Employee employee = new Employee(usern, passw, id, fname, lname, level, type, false);
        /*query = "SELECT type FROM \"EmployeeType\" WHERE username = ?";
        connection = DataManager.DbConnection();
        statement = connection.prepareStatement(query);
        int one = 1;
        statement.setString(one, employee.getUsername());
        ResultSet rs2 = statement.executeQuery();
        while (rs2.next()) {
          employee.addType(EmployeeType.valueOf(rs2.getString("type")));
        }*/
        list.add(employee);
      }
    } catch (SQLException e) {
      System.out.println("Get all Employees error.");
    }
    return list;
  }

  public ArrayList<String> getAllUsernames() throws SQLException {
    Connection connection = DataManager.DbConnection();
    ArrayList<String> list = new ArrayList<String>();
    try {
      String query = "SELECT * FROM \"Employee\"";
      PreparedStatement statement = connection.prepareStatement(query);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        String usern = rs.getString("username");
        list.add(usern);
      }
    } catch (SQLException e) {
      System.out.println("Get all Employees error.");
    }
    return list;
  }

  /**
   * This method adds a new Employee object to the "Employee" table in the database.
   *
   * @param employee the Employee object to be added to the "Employee" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void add(Employee employee) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query =
        "INSERT INTO \"Employee\" (username, password, \"employeeID\", \"firstName\", \"lastName\", \"clearanceLevel\", type) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    try {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setString(1, employee.getUsername());
      statement.setString(2, employee.getPassword());
      statement.setInt(3, employee.getEmployeeID());
      statement.setString(4, employee.getFirstName());
      statement.setString(5, employee.getLastName());
      statement.setString(6, employee.getLevel().getString());
      statement.setString(7, employee.getType().getString());
      statement.executeUpdate();

      /*for (EmployeeType type : employee.getType()) {
        query = "INSERT INTO \"EmployeeType\" (username, type) VALUES (?, ?)";
        connection = DataManager.DbConnection();
        statement = connection.prepareStatement(query);
        statement.setString(1, employee.getUsername());
        statement.setString(2, type.getString());
        statement.executeUpdate();
      }*/
      System.out.println("Employee information has been successfully added to the database.");
    } catch (SQLException e) {
      System.err.println("Error adding Login information to database: " + e.getMessage());
    }
  }

  /**
   * This method deletes the given Employee object from the database
   *
   * @param employee the Employee object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void delete(Employee employee) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query = "Delete from \"Employee\" where username = ?";

    try {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setString(1, employee.getUsername());
      statement.executeUpdate();

      /*query = "DELETE FROM \"EmployeeType\" WHERE username = ?";
      connection = DataManager.DbConnection();
      statement = connection.prepareStatement(query);
      statement.setString(1, employee.getUsername());
      statement.executeUpdate();*/
    } catch (SQLException e) {
      System.out.println("Delete in Login table error. " + e);
    }

    /*try (Statement statement = connection.createStatement()) {
      ResultSet rs2 = statement.executeQuery(query);
      int count = 0;
      while (rs2.next()) count++;
      if (count == 0) System.out.println("Login information deleted successfully.");
      else System.out.println("Login information did not delete.");
    } catch (SQLException e2) {
      System.out.println("Error checking delete. " + e2);
    }*/
  }

  /**
   * This method retrieves a Employee object with the specified username from the "Employee" table
   * in the database.
   *
   * @param username the username of the username object to retrieve from the "Employee" table
   * @return the Login object with the specified username, or null if not found
   * @throws SQLException if there is a problem accessing the database
   */
  public static Employee getEmployee(String username) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"Employee\" WHERE \"username\" = ?";
    Employee employee = null;
    try {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setString(1, username);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        String user = rs.getString("username");
        String pass = rs.getString("password");
        int id = rs.getInt("employeeID");
        String fname = rs.getString("firstName");
        String lname = rs.getString("lastName");
        EmployeeType type = EmployeeType.valueOf(rs.getString("type"));
        ClearanceLevel level = ClearanceLevel.valueOf(rs.getString("clearanceLevel"));
        employee = (new Employee(user, pass, id, fname, lname, level, type, false));
      }
      /*query = "SELECT type FROM \"EmployeeType\" WHERE \"username\" = ?";
      connection = DataManager.DbConnection();
      statement = connection.prepareStatement(query);
      statement.setString(1, username);
      ResultSet rs2 = statement.executeQuery();
      while (rs2.next()) {
        employee.addType(EmployeeType.valueOf(rs2.getString("type")));
      }*/
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return employee;
  }

  /**
   * Uploads CSV data to a PostgreSQL database table "Employee"
   *
   * @param csvFilePath is a String representing a file path
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadEmployeeToPostgreSQL(String csvFilePath)
      throws SQLException, ParseException {
    List<String[]> csvData;
    Connection connection = DataManager.DbConnection();
    DataManager dataImport = new DataManager();
    csvData = dataImport.parseCSVAndUploadToPostgreSQL(csvFilePath);

    try (connection) {
      String query =
          "INSERT INTO \"Employee\" (\"username\", \"password\", \"employeeID\", \"firstName\", \"lastName\", \"clearanceLevel\", type) VALUES (?, ?, ?, ?, ?, ?, ?)";
      PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE \"Employee\";");
      statement.executeUpdate();
      statement = connection.prepareStatement(query);

      for (int i = 1; i < csvData.size(); i++) {
        String[] row = csvData.get(i);
        statement.setString(1, row[0]); // username is a string column
        statement.setString(2, row[1]); // password is a string column
        statement.setInt(3, Integer.parseInt(row[2]));
        statement.setString(4, row[3]);
        statement.setString(5, row[4]);
        statement.setString(6, row[5]);
        statement.setString(7, row[6]);

        statement.executeUpdate();
      }
      System.out.println("CSV data uploaded to PostgreSQL database");
    } catch (SQLException e) {
      System.err.println("Error uploading CSV data to PostgreSQL database: " + e.getMessage());
    }
  }

  /**
   * Uploads CSV data to a PostgreSQL database table "EmployeeType"
   *
   * @param csvFilePath is a String representing a file path
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  /*public static void uploadEmployeeTypeToPostgreSQL(String csvFilePath)
      throws SQLException, ParseException {
    List<String[]> csvData;
    Connection connection = DataManager.DbConnection();
    DataManager dataImport = new DataManager();
    csvData = dataImport.parseCSVAndUploadToPostgreSQL(csvFilePath);

    try (connection) {
      String query = "INSERT INTO \"EmployeeType\" (\"username\", type) VALUES (?, ?)";
      PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE \"EmployeeType\";");
      statement.executeUpdate();
      statement = connection.prepareStatement(query);

      for (int i = 1; i < csvData.size(); i++) {
        String[] row = csvData.get(i);
        statement.setString(1, row[0]); // username is a string column
        statement.setString(2, row[1]); // type is a string column
        statement.executeUpdate();
      }
      System.out.println("CSV data uploaded to PostgreSQL database");
    } catch (SQLException e) {
      System.err.println("Error uploading CSV data to PostgreSQL database: " + e.getMessage());
    }
  }*/

  /**
   * Exports data from a PostgreSQL database table "Employee" to a CSV file
   *
   * @param csvFilePath is a String representing a file path
   * @throws SQLException if an error occurs while exporting the data from the database
   */
  public static void exportEmployeeToCSV(String csvFilePath) throws SQLException, IOException {
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"Employee\"";
    PreparedStatement statement = connection.prepareStatement(query);
    ResultSet resultSet = statement.executeQuery();

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
      writer.write("username,password,employeeID,firstName,lastName,clearanceLevel,type\n");
      while (resultSet.next()) {
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        int employeeID = resultSet.getInt("employeeID");
        String fname = resultSet.getString("firstName");
        String lname = resultSet.getString("lastName");
        String level = resultSet.getString("clearanceLevel");
        String type = resultSet.getString("type");

        String row =
            username
                + ","
                + password
                + ","
                + employeeID
                + ","
                + fname
                + ","
                + lname
                + ","
                + level
                + ","
                + type
                + "\n";
        writer.write(row);
      }
      System.out.println("CSV data downloaded from PostgreSQL database");
    } catch (IOException e) {
      System.err.println("Error downloading CSV data from PostgreSQL database: " + e.getMessage());
    }
  }

  /**
   * Exports data from a PostgreSQL database table "EmployeeType" to a CSV file
   *
   * @param csvFilePath is a String representing a file path
   * @throws SQLException if an error occurs while exporting the data from the database
   */
  /*public static void exportEmployeeTypeToCSV(String csvFilePath) throws SQLException, IOException {
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"EmployeeType\"";
    PreparedStatement statement = connection.prepareStatement(query);
    ResultSet resultSet = statement.executeQuery();

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
      writer.write("username,type\n");
      while (resultSet.next()) {
        String username = resultSet.getString("username");
        String type = resultSet.getString("type");

        String row = username + "," + type + "\n";
        writer.write(row);
      }
      System.out.println("CSV data downloaded from PostgreSQL database");
    } catch (IOException e) {
      System.err.println("Error downloading CSV data from PostgreSQL database: " + e.getMessage());
    }
  }*/

  /**
   * conencts to the employee database and checks if the given username and pass are valid returns
   * the Employee if it exists
   *
   * @param user the username to check
   * @param pass the password to check
   * @return the Employee if it exists and is correct, null if no
   * @throws SQLException
   */
  public static Employee checkLogin(String user, String pass) throws SQLException {
    Employee employee = getEmployee(user);
    if (employee == null) {
      return null;
    } else {
      if (employee.getPassword().equals(Employee.hash(pass))) {
        return employee;
      } else {
        return null;
      }
    }
  }

  /*public static void addEmployeeType(String username, EmployeeType employeeType)
      throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query = "INSERT INTO \"EmployeeType\" (\"username\", \"type\") VALUES (?, ?)";
    PreparedStatement statement = connection.prepareStatement(query);

    statement.setString(1, username);
    statement.setString(2, employeeType.name());

    statement.executeUpdate();
  }
  public static void deleteEmployeeType(String username) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query = "DELETE FROM \"EmployeeType\" WHERE \"username\" = ?";
    PreparedStatement statement = connection.prepareStatement(query);

    statement.setString(1, username);

    statement.executeUpdate();
  }*/
}

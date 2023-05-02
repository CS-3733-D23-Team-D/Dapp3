package edu.wpi.teamname.database;

import edu.wpi.teamname.database.interfaces.FeedbackDAO;
import edu.wpi.teamname.employees.Feedback;
import edu.wpi.teamname.servicerequest.Status;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAOImpl implements FeedbackDAO {

  /**
   * This method updates an existing Move object in the "Feedback" table in the database with the
   * new Feedback object.
   *
   * @param feedback the new Feedback object to be updated in the "Feedback" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void sync(Feedback feedback) throws SQLException {
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query =
          "UPDATE \"Feedback\" SET \"reporter\" = ?, \"dateReported\" = ?, description = ?, assignee=?, status=?"
              + " WHERE id = ? AND \"reporter\" = ? AND \"dateReported\" = ? AND description = ? AND \"assignee\" = ? AND\"status\" = ?";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setString(1, feedback.getReporter());
      statement.setTimestamp(2, feedback.getDateReported());
      statement.setString(3, feedback.getDescription());
      statement.setString(4, feedback.getAssignee());
      statement.setString(5, feedback.getStatus().getStatusString());
      statement.setInt(6, feedback.getId()); // add the id parameter

      // set the remaining parameters
      statement.setString(7, feedback.getReporter());
      statement.setTimestamp(8, feedback.getDateReported());
      statement.setString(9, feedback.getDescription());
      statement.setString(10, feedback.getAssignee());
      statement.setString(11, feedback.getStatus().getStatusString());

      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    connection.close();
  }

  /**
   * The method retrieves all the Feedback objects from the "Feedback" table in the database.
   *
   * @return an ArrayList of the Feedback objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public ArrayList<Feedback> getAll() throws SQLException {
    Connection connection = DataManager.DbConnection();
    ArrayList<Feedback> list = new ArrayList<Feedback>();
    try (connection) {
      String query = "SELECT * FROM \"Feedback\"";
      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery(query);

      while (rs.next()) {
        String reporter = rs.getString("reporter");
        Timestamp date = rs.getTimestamp("dateReported");
        String description = rs.getString("description");
        String assignee = rs.getString("assignee");
        Status status = Status.valueOf(rs.getString("status"));
        int id = rs.getInt("id");
        list.add(new Feedback(reporter, description));
      }
    } catch (SQLException e) {
      System.out.println("Get all nodes error.");
    }
    return list;
  }

  /**
   * This method adds a new Feedback object to the "Feedback" table in the database.
   *
   * @param feedback the Feedback object to be added to the "Feedback" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void add(Feedback feedback) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query =
        "INSERT INTO \"Feedback\" (id, reporter, \"dateReported\", description, assignee, status) "
            + "VALUES (DEFAULT, ?, ?, ?, ?, ?)";

    try (connection) {
      PreparedStatement statement =
          connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      statement.setString(1, feedback.getReporter());
      statement.setTimestamp(2, feedback.getDateReported());
      statement.setString(3, feedback.getDescription());
      statement.setString(4, feedback.getAssignee());
      statement.setString(5, feedback.getStatus().getStatusString());
      statement.executeUpdate();

      ResultSet rs = statement.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        feedback.setId(id);
      }

      System.out.println("Feedback information has been successfully added to the database.");
    } catch (SQLException e) {
      System.err.println("Error adding feedback information to database: " + e.getMessage());
    } finally {
      connection.close();
    }
  }

  /**
   * This method deletes the given Feedback object from the database
   *
   * @param feedback the Feedback object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void delete(Feedback feedback) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String del = "DELETE ";
    String sel = "SELECT * ";
    String query =
        "FROM \"Feedback\" WHERE id = ? AND reporter = ? AND \"dateReported\" = ? AND description = ? AND assignee = ? AND status = ?";

    try (PreparedStatement statement = connection.prepareStatement(del + query)) {
      statement.setInt(1, feedback.getId()); // add the id parameter
      statement.setString(2, feedback.getReporter());
      statement.setTimestamp(3, feedback.getDateReported());
      statement.setString(4, feedback.getDescription());
      statement.setString(5, feedback.getAssignee());
      statement.setString(6, feedback.getStatus().getStatusString());
      statement.executeUpdate();
      System.out.println("Feedback information has been successfully deleted from the database.");
    } catch (SQLException e) {
      System.err.println("Error deleting feedback information from database: " + e.getMessage());
    } finally {
      connection.close();
    }
  }

  /**
   * Imports feedback data from a CSV file and uploads it to the "Feedback" table in the database.
   *
   * @param csvFilePath The path to the CSV file containing feedback data.
   * @throws SQLException If there is an error importing the data to the database.
   */
  public static void importFeedbackFromCSV(String csvFilePath) throws SQLException {
    List<String[]> csvData;
    Connection connection = DataManager.DbConnection();
    DataManager dataImport = new DataManager();
    csvData = dataImport.parseCSVAndUploadToPostgreSQL(csvFilePath);

    try (connection) {
      String createTableQuery =
          "CREATE TABLE IF NOT EXISTS \"Feedback\" ("
              + "\"reporter\" varchar(255),"
              + "\"description\" varchar(255),"
              + "\"dateReported\" timestamp,"
              + "\"assignee\" varchar(255),"
              + "\"id\" SERIAL PRIMARY KEY,"
              + "\"status\" varchar(255)"
              + ");";
      PreparedStatement createTableStatement = connection.prepareStatement(createTableQuery);
      createTableStatement.execute();

      String query =
          "INSERT INTO \"Feedback\" (\"reporter\", \"description\", \"dateReported\", \"assignee\", \"status\") "
              + "VALUES (?, ?, ?, ?, ?)";
      PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE \"Feedback\";");
      statement.executeUpdate();
      statement = connection.prepareStatement(query);

      for (int i = 1; i < ((List<?>) csvData).size(); i++) {
        String[] row = csvData.get(i);
        statement.setString(1, row[0]);
        statement.setString(2, row[1]);
        String timestampString = row[2];
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy H:mm");
        java.util.Date parsedDate = dateFormat.parse(timestampString);
        java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
        statement.setTimestamp(3, timestamp);
        statement.setString(4, row[3]);
        statement.setString(5, row[4]);
        statement.executeUpdate();
      }
      System.out.println("CSV data imported to PostgreSQL database");
    } catch (SQLException e) {
      System.err.println("Error importing CSV data to PostgreSQL database: " + e.getMessage());
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Exports feedback data from the "Feedback" table in the database to a CSV file.
   *
   * @param csvFilePath The path to the CSV file to export the data to.
   * @throws SQLException If there is an error exporting the data from the database.
   * @throws IOException If there is an error writing the data to the CSV file.
   */
  public static void exportFeedbackToCSV(String csvFilePath) throws SQLException, IOException {
    List<String[]> csvData = new ArrayList<>();
    Connection connection = DataManager.DbConnection();

    try (connection) {
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery("SELECT * FROM \"Feedback\"");

      // add column headers
      ResultSetMetaData metaData = resultSet.getMetaData();
      int columnCount = metaData.getColumnCount();
      String[] headers = new String[columnCount];
      for (int i = 1; i <= columnCount; i++) {
        headers[i - 1] = metaData.getColumnName(i);
      }
      csvData.add(headers);

      // add data rows
      while (resultSet.next()) {
        String[] row = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
          Object value = resultSet.getObject(i);
          row[i - 1] = value == null ? "" : value.toString();
        }
        csvData.add(row);
      }

      // write data to CSV file
      FileWriter fileWriter = new FileWriter(csvFilePath);
      for (String[] row : csvData) {
        for (int i = 0; i < row.length; i++) {
          fileWriter.append("\"");
          fileWriter.append(row[i].replace("\"", "\"\""));
          fileWriter.append("\"");
          if (i < row.length - 1) {
            fileWriter.append(",");
          }
        }
        fileWriter.append("\n");
      }
      fileWriter.flush();
      fileWriter.close();

      System.out.println("Data exported from PostgreSQL database to CSV file");
    } catch (SQLException | IOException e) {
      System.err.println("Error exporting data from PostgreSQL database: " + e.getMessage());
      throw e;
    }
  }

  /**
   * Creates a new "Feedback" table in the connected PostgreSQL database if it does not already
   * exist.
   *
   * @throws SQLException if there is an error executing the SQL query.
   */
  public static void createTable() throws SQLException {
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query =
          "CREATE TABLE IF NOT EXISTS \"Feedback\" ("
              + "    \"reporter\" varchar(255),"
              + "    \"description\" varchar(255),"
              + "    \"dateReported\" timestamp,"
              + "    \"assignee\" varchar(255),"
              + "    \"id\" SERIAL PRIMARY KEY,"
              + "    \"status\" varchar(255)"
              + ");";

      PreparedStatement statement = connection.prepareStatement(query);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
  }
}

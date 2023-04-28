package edu.wpi.teamname.database;

import edu.wpi.teamname.database.interfaces.SignageDAO;
import edu.wpi.teamname.navigation.Direction;
import edu.wpi.teamname.navigation.Signage;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SignageDAOImpl implements SignageDAO {
  /**
   * Gets the signage based on specific kiosk and date from signage table Configures arrows as well
   *
   * @param kiosk
   * @param date
   * @return list of strings
   * @throws SQLException
   */
  public ArrayList<String> getSignage(int kiosk, Timestamp date) throws SQLException {
    ArrayList<String> items = new ArrayList<>();
    Connection connection = DataManager.DbConnection();
    String query = "Select *\n" + "From \"Signage\"\n" + "Where \"kioskID\" = ? AND \"date\" = ?";
    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, kiosk);
      statement.setTimestamp(2, date);

      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        String lName = rs.getString("longName");
        String dir = rs.getString("arrowDirection");
        int kioskID = rs.getInt("kioskID");
        int signID = rs.getInt("signID");

        items.add(dir + "|" + lName);
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return items;
  }

  /**
   * Gets the kiosk ID based on date selected from signage table
   *
   * @param date
   * @return list of int
   * @throws SQLException
   */
  public ArrayList<Integer> getKiosks(Timestamp date) throws SQLException {
    ArrayList<Integer> items = new ArrayList<>();
    Connection connection = DataManager.DbConnection();
    String query =
        "Select \"kioskID\"\n" + "From \"Signage\"\n" + "Where \"date\" = ? Group by \"kioskID\"";
    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setTimestamp(1, date);

      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        int kioskID = rs.getInt("kioskID");
        items.add(kioskID);
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return items;
  }

  /**
   * This method updates an existing Signage object in the "Signage" table in the database with the
   * new Signage object.
   *
   * @param signage the new Signage object to be updated in the "Signage" table
   * @throws SQLException if there is a problem accessing the database
   */
  public void sync(Signage signage) throws SQLException {
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query =
          "UPDATE \"Signage\" SET \"shortName\" = ?, \"date\" = ?, \"endDate\" = ?, \"arrowDirection\" = ?, \"signID\" = ?, \"kioskID\" = ?"
              + " WHERE \"longName\" = ?";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setString(1, signage.getShortName());
      statement.setTimestamp(2, signage.getDate());
      statement.setTimestamp(3, signage.getEndDate());
      statement.setString(4, signage.getArrowDirection().name());
      statement.setInt(5, signage.getSignId());
      statement.setInt(6, signage.getKioskId());
      statement.setString(7, signage.getLongName());

      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    connection.close();
  }

  /**
   * The method retrieves all the Signage objects from the "Signage" table in the database.
   *
   * @return an ArrayList of the Signage objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public ArrayList<Signage> getAll() throws SQLException {
    Connection connection = DataManager.DbConnection();
    ArrayList<Signage> list = new ArrayList<Signage>();
    try {
      String query = "SELECT * FROM \"Signage\"";
      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery(query);

      while (rs.next()) {
        String longn = rs.getString("longName");
        String shortn = rs.getString("shortName");
        Timestamp date = rs.getTimestamp("date");
        Direction arrowDirection = Direction.valueOf(rs.getString("arrowDirection"));
        int signId = rs.getInt("signID");
        int kioskId = rs.getInt("kioskID");
        Timestamp endDate = rs.getTimestamp("endDate");
        list.add(new Signage(longn, shortn, date, endDate, arrowDirection, signId, kioskId));
      }
    } catch (SQLException e) {
      System.out.println("Get all Signage error.");
    }
    return list;
  }

  /**
   * This method adds a new Signage object to the "Signage" table in the database.
   *
   * @param signage the Signage object to be added to the "Signage" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void add(Signage signage) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query =
        "INSERT INTO \"Signage\" (\"longName\", \"shortName\", \"date\", \"endDate\", \"arrowDirection\", \"signID\", \"kioskID\") "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setString(1, signage.getLongName());
      statement.setString(2, signage.getShortName());
      statement.setTimestamp(3, signage.getDate());
      statement.setTimestamp(4, signage.getEndDate());
      statement.setString(5, signage.getArrowDirection().toString());
      statement.setInt(6, signage.getSignId());
      statement.setInt(7, signage.getKioskId());
      statement.executeUpdate();
      System.out.println("Signage information has been successfully added to the database.");
    } catch (SQLException e) {
      System.err.println(query);
      System.err.println("Error adding Signage information to database: " + e.getMessage());
    }
  }
  /**
   * Deletes the given Signage object from the "Signage" table in the database
   *
   * @param signage the Signage object to be deleted from the "Signage" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void delete(Signage signage) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String del = "DELETE ";
    String sel = "SELECT * ";
    String query = "FROM \"Signage\" WHERE \"signID\" = ?";

    try (PreparedStatement statement = connection.prepareStatement(del + query)) {
      statement.setInt(1, signage.getSignId());
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Delete in Signage table error. " + e);
    }

    try (Statement statement = connection.createStatement()) {
      ResultSet rs2 = statement.executeQuery(sel + query);
      int count = 0;
      while (rs2.next()) count++;
      if (count == 0) System.out.println("Signage information deleted successfully.");
      else System.out.println("Signage information did not delete.");
    } catch (SQLException e) {
      System.err.println("Error importing CSV data to PostgreSQL database: " + e.getMessage());
    }
  }
  /**
   * Imports data from a CSV file to the "Signage" table in the database
   *
   * @param csvFilePath a String representing the csv data (must use "//" not "/")
   * @throws SQLException if an error occurs while importing the data to the database
   */
  public static void importSignageFromCSV(String csvFilePath) throws SQLException {
    List<String[]> csvData;
    Connection connection = DataManager.DbConnection();
    DataManager dataImport = new DataManager();
    csvData = dataImport.parseCSVAndUploadToPostgreSQL(csvFilePath);

    try (connection) {
      String createTableQuery =
          "CREATE TABLE IF NOT EXISTS \"Signage\" ("
              + "\"longName\" varchar(255),"
              + "\"shortName\" varchar(255),"
              + "\"date\" timestamp,"
              + "\"arrowDirection\" varchar(255),"
              + "\"kioskID\" int,"
              + "\"signID\" SERIAL PRIMARY KEY"
              + ");";
      PreparedStatement createTableStatement = connection.prepareStatement(createTableQuery);
      createTableStatement.execute();

      String query =
          "INSERT INTO \"Signage\" (\"longName\", \"shortName\", \"date\", \"endDate\", \"arrowDirection\", \"signID\", \"kioskID\") "
              + "VALUES (?, ?, ?, ?, ?, ?, ?)";
      PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE \"Signage\";");
      statement.executeUpdate();
      statement = connection.prepareStatement(query);

      for (int i = 1; i < csvData.size(); i++) {
        String[] row = csvData.get(i);
        statement.setString(1, row[0]); // longName is a string column
        statement.setString(2, row[1]); // shortName is a string column
        String timestampString = row[2];
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy H:mm");
        java.util.Date parsedDate = dateFormat.parse(timestampString);
        java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
        statement.setTimestamp(3, timestamp);
        String timestampEndString = row[3];
        java.util.Date parsedEndDate = dateFormat.parse(timestampEndString);
        java.sql.Timestamp endDate = new java.sql.Timestamp(parsedEndDate.getTime());
        statement.setTimestamp(4, endDate);
        Direction arrowDirection =
            Direction.valueOf(row[4].toUpperCase()); // arrowDirection is now an enum
        statement.setString(
            5,
            arrowDirection.toString()); // convert the enum to a string for storage in the database
        statement.setInt(6, Integer.parseInt(row[5]));
        statement.setInt(7, Integer.parseInt(row[6]));
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
   * Exports data from a PostgreSQL database table "Signage" to a CSV file
   *
   * @param csvFilePath a String representing the csv data (must use "//" not "/")
   * @throws SQLException if an error occurs while exporting the data from the database
   * @throws IOException if an error occurs while writing the data to the file
   */
  public static void exportSignageToCSV(String csvFilePath) throws SQLException, IOException {
    List<String[]> csvData = new ArrayList<>();
    Connection connection = DataManager.DbConnection();

    try (connection) {
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery("SELECT * FROM \"Signage\"");

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
   * * Creates a table for storing Signage data if it doesn't already exist
   *
   * @throws SQLException if connection to the database fails
   */
  public static void createTable() throws SQLException {
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query =
          "create table if not exists \"Signage\"\n"
              + "(\n"
              + "    \"longName\"       text,\n"
              + "    \"shortName\"      text,\n"
              + "    date             timestamp,\n"
              + "    \"endDate\"             timestamp,\n"
              + "    \"signID\"         integer not null\n"
              + "        constraint \"Signage_pk\"\n"
              + "            primary key,\n"
              + "    \"arrowDirection\" text,\n"
              + "    \"kioskID\"        integer\n"
              + ");";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
  }
}

package edu.wpi.teamname.database;

import edu.wpi.teamname.alerts.Alert;
import edu.wpi.teamname.database.interfaces.AlertDAO;
import edu.wpi.teamname.employees.EmployeeType;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AlertDAOImpl implements AlertDAO {
  @Override
  public void sync(Alert alert) throws SQLException {
    Connection connection = DataManager.DbConnection();
    try {
      String query =
          "UPDATE \"Alert\" SET \"alertID\" = ?, \"startDate\" = ?, \"endDate\" = ?, \"creator\" = ?, \"description\" = ?, \"announcement\" = ?, \"employeeType\" = ?, \"urgency\" = ?";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setLong(1, alert.getId());
      statement.setTimestamp(2, alert.getStartDisplayDate());
      statement.setTimestamp(3, alert.getEndDisplayDate());
      statement.setString(4, alert.getCreator());
      statement.setString(5, alert.getDescription());

      statement.setString(6, alert.getAnnouncement());
      statement.setString(7, alert.getType().getString());
      statement.setString(8, alert.getUrgency().getString());

      statement.executeUpdate();

    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
    connection.close();
  }

  @Override
  public ArrayList<Alert> getAll() throws SQLException {
    Connection connection = DataManager.DbConnection();
    ArrayList<Alert> list = new ArrayList<Alert>();

    try (connection) {
      String query = "SELECT * FROM \"Alert\"";
      PreparedStatement statement = connection.prepareStatement(query);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        int alertID = rs.getInt("alertID");
        Timestamp startDate = rs.getTimestamp("startDate");
        Timestamp endDate = rs.getTimestamp("endDate");
        String creator = rs.getString("creator");
        String description = rs.getString("description");
        String announcement = rs.getString("announcement");
        EmployeeType employeeType = EmployeeType.valueOf(rs.getString("employeeType"));
        Alert.Urgency urgency = Alert.Urgency.valueOf(rs.getString("urgency"));

        list.add(
            new Alert(
                alertID,
                startDate,
                endDate,
                creator,
                description,
                announcement,
                employeeType,
                urgency));
      }
    }
    connection.close();
    return list;
  }

  @Override
  public void add(Alert alert) throws SQLException {
    Connection connection = DataManager.DbConnection();
    try {
      String query =
          "INSERT INTO \"Alert\" (\"alertID\", \"startDate\", \"endDate\", \"creator\", \"description\", \"announcement\", \"employeeType\", \"urgency\") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setLong(1, alert.getId());
      statement.setTimestamp(2, alert.getStartDisplayDate());
      statement.setTimestamp(3, alert.getEndDisplayDate());
      statement.setString(4, alert.getCreator());
      statement.setString(5, alert.getDescription());

      statement.setString(6, alert.getAnnouncement());
      statement.setString(7, alert.getType().getString());
      statement.setString(8, alert.getUrgency().getString());

      statement.executeUpdate();

    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
    connection.close();
  }

  public ArrayList<Integer> getAllIDs() throws SQLException {
    Connection connection = DataManager.DbConnection();
    ArrayList<Integer> list = new ArrayList<Integer>();

    try (connection) {
      String query = "SELECT * FROM \"Alert\"";
      PreparedStatement statement = connection.prepareStatement(query);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        int alertID = rs.getInt("alertID");
        list.add((alertID));
      }
    }
    connection.close();
    return list;
  }

  @Override
  public void delete(Alert alert) throws SQLException {}

  /**
   * * Creates a table for storing Alert data if it doesn't already exist
   *
   * @throws SQLException if connection to the database fails
   */
  public static void createTable() throws SQLException {
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query =
          "create table if not exists \"Alert\"\n"
              + "(\n"
              + "    \"alertID\"      integer,\n"
              + "    \"startDate\"    timestamp,\n"
              + "    \"endDate\"      timestamp,\n"
              + "    creator        varchar(16),\n"
              + "    description    varchar(256),\n"
              + "    announcement   varchar(64),\n"
              + "    \"employeeType\" varchar(20),\n"
              + "    urgency        varchar(10)\n"
              + ");";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
  }

  public static void exportAlertToCSV(String csvFilePath) throws SQLException, IOException {
    AlertDAOImpl AlertDao = new AlertDAOImpl();
    ArrayList<Alert> alerts = AlertDao.getAll();

    FileWriter writer = new FileWriter(csvFilePath);
    writer.write(
        "Alert ID,Creator Name,Start Date,End Date,Employee Type,Announcement,Description,Urgency\n");

    for (Alert al : alerts) {
      writer.write(
          al.getId()
              + ","
              + al.getCreator()
              + ","
              + al.getStartDisplayDate()
              + ","
              + al.getEndDisplayDate()
              + ","
              + al.getType()
              + ","
              + al.getAnnouncement()
              + ","
              + al.getDescription()
              + ","
              + al.getUrgency()
              + "\n");
    }
    writer.close();
  }

  public static void uploadAlertToPostgreSQL(String csvFilePath) throws SQLException {
    List<String[]> csvData;
    Connection connection = DataManager.DbConnection();
    DataManager dataImport = new DataManager();
    csvData = dataImport.parseCSVAndUploadToPostgreSQL(csvFilePath);

    try (connection) {
      DatabaseMetaData dbm = connection.getMetaData();
      ResultSet tables = dbm.getTables(null, null, "Node", null);
      if (!tables.next()) {
        PreparedStatement createTableStatement =
            connection.prepareStatement(
                "CREATE TABLE \"Alert\" (\"alertID\" INTEGER NOT NULL, \"startDate\" TIMESTAMP NOT NULL, \"endDate\" TIMESTAMP NOT NULL, \"creator\" VARCHAR(30) NOT NULL, \"description\" VARCHAR(256) NOT NULL, \"announcement\" VARCHAR(64) NOT NULL, \"employeeType\" VARCHAR(20) NOT NULL, \"urgency\" VARCHAR(10), PRIMARY KEY (\"alertID\"))");
        createTableStatement.executeUpdate();
      }

      String query =
          "INSERT INTO \"Alert\" (\"alertID\", \"startDate\", \"endDate\", \"creator\", \"description\", \"announcement\", \"employeeType\", \"urgency\") "
              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
      PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE \"ALERT\";");
      statement.executeUpdate();
      statement = connection.prepareStatement(query);

      for (int i = 1; i < csvData.size(); i++) {
        String[] row = csvData.get(i);

        // Check if row has enough columns
        if (row.length < 8) {
          System.err.println("Skipping row " + i + " due to missing columns");
          continue;
        }

        statement.setInt(1, Integer.parseInt(row[0]));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        java.util.Date parsedDate = dateFormat.parse(row[1]);
        java.sql.Date date = new java.sql.Date(parsedDate.getTime());
        statement.setDate(2, date);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        java.util.Date parsedDate2 = dateFormat2.parse(row[2]);
        java.sql.Date date2 = new java.sql.Date(parsedDate2.getTime());
        statement.setDate(3, date2);
        statement.setString(4, row[3]);
        statement.setString(5, row[4]);
        statement.setString(6, row[5]);
        statement.setString(7, row[6]);
        statement.setString(8, row[7]);

        statement.executeUpdate();
      }
      System.out.println("CSV data uploaded to PostgreSQL database");
    } catch (SQLException e) {
      System.err.println("Error uploading CSV data to PostgreSQL database: " + e.getMessage());
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }
}

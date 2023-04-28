package edu.wpi.teamname.database;

import edu.wpi.teamname.database.interfaces.ConfReservationDAO;
import edu.wpi.teamname.servicerequest.ConfReservation;
import edu.wpi.teamname.servicerequest.requestitem.ConfRoom;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/** LINK FUNCTION TO DATAMANAGER */
public class ConfReservationDAOImpl implements ConfReservationDAO {

  /**
   * sets the reservation ID in conference reservations table
   *
   * @return int reservationID
   * @throws SQLException
   */
  public int setResID() throws SQLException {
    int resID = -1;
    Connection connection = DataManager.DbConnection();
    String query = "Select max(\"roomID\") From \"ConfReservations\"";
    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      ResultSet rs = statement.executeQuery();
      rs.next();
      resID = rs.getInt("resID") + 1;
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return resID;
  }

  /**
   * Gets all the reservations for a conference rooom in conference reservations table
   *
   * @param confrom
   * @return List of conference reservations
   * @throws SQLException
   */
  public ArrayList<ConfReservation> getResForRoom(ConfRoom confrom) throws SQLException {
    int confID = confrom.getRoomID();
    ArrayList<ConfReservation> rooms = new ArrayList<>();
    Connection connection = DataManager.DbConnection();
    String query = "Select * From \"ConfReservations\" Where \"roomID\" = ?";
    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, confID);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        int resID = rs.getInt("resID");
        String startT = rs.getString("starttime");
        String endT = rs.getString("endtime");
        Timestamp dateBook = rs.getTimestamp("datebook");
        Timestamp dateMade = rs.getTimestamp("dateMade");
        String name = rs.getString("name");
        String username = rs.getString("username");
        String staff = rs.getString("staffAssigned");
        int roomID = rs.getInt("roomID");
        ConfReservation res =
            new ConfReservation(
                resID, startT, endT, dateBook, dateMade, name, username, staff, roomID);
        rooms.add(res);
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return rooms;
  }

  /**
   * Syncs the conference reservation table with incoming conference reservation objects
   *
   * @param ConfReservation
   * @throws SQLException
   */
  @Override
  public void sync(ConfReservation ConfReservation) throws SQLException {
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query =
          "UPDATE \"ConfReservations\" SET \"resID\" = ?, \"starttime\" = ?, \"endtime\" = ?, datebook = ?, \"name\" = ?, username = ?, \"staffAssigned\" = ?, \"dateMade\" = ?, \"roomID\" = ?"
              + "  WHERE \"resID\" = ? AND \"starttime\" = ? AND \"endtime\" = ? AND datebook = ? AND \"name\" = ? AND username = ? AND \"staffAssigned\" = ? AND \"dateMade\" = ? AND \"roomID\" = ?";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, ConfReservation.getResID());
      statement.setString(2, ConfReservation.getStartTime());
      statement.setString(3, ConfReservation.getEndTime());
      statement.setTimestamp(4, ConfReservation.getDateBook());
      statement.setString(5, ConfReservation.getName());
      statement.setString(6, ConfReservation.getUsername());
      statement.setString(7, ConfReservation.getStaffAssigned());
      statement.setTimestamp(8, ConfReservation.getDateMade());
      statement.setInt(9, ConfReservation.getRoomID());

      statement.setInt(10, ConfReservation.getOrigResID());
      statement.setString(11, ConfReservation.getOrigStartTime());
      statement.setString(12, ConfReservation.getOrigEndTime());
      statement.setTimestamp(13, ConfReservation.getOrigDateBook());
      statement.setString(14, ConfReservation.getOrigName());
      statement.setString(15, ConfReservation.getOrigUsername());
      statement.setString(16, ConfReservation.getOrigStaffAssigned());
      statement.setTimestamp(17, ConfReservation.getOrigDateMade());
      statement.setInt(18, ConfReservation.getOrigRoomID());

      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    connection.close();
  }

  /**
   * The method retrieves all the ConfReservation objects from the "ConferenceRooms" table in the
   * database.
   *
   * @return an ArrayList of the ConfReservation objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public ArrayList<ConfReservation> getAll() throws SQLException {
    Connection connection = DataManager.DbConnection();
    ArrayList<ConfReservation> list = new ArrayList<ConfReservation>();

    try (connection) {
      String query = "SELECT * FROM \"ConfReservations\"";
      PreparedStatement statement = connection.prepareStatement(query);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        int resID = rs.getInt("resID");
        String startTime = rs.getString("starttime");
        String endTime = rs.getString("endtime");
        Timestamp dateBook = rs.getTimestamp("datebook");
        String name = rs.getString("name");
        String username = rs.getString("dateMade");
        String staff = rs.getString("staffAssigned");
        Timestamp dateMade = rs.getTimestamp("dateMade");
        int roomID = rs.getInt("roomID");
        list.add(
            new ConfReservation(
                resID, startTime, endTime, dateBook, dateMade, name, username, staff, roomID));
      }
    }
    connection.close();
    return list;
  }

  /**
   * This method adds a new ConfReservation object to the "ConferenceRooms" table in the database.
   *
   * @param ConfReservation the ConfReservation object to be added to the "ConfReservation" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void add(ConfReservation ConfReservation)
      throws SQLException { // WILL NOT USE UNLESS A CONFERENCE ROOM IS BEING ADDED
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query =
          "INSERT INTO \"ConfReservations\" (\"resID\", \"starttime\", \"endtime\", datebook, \"name\", username, \"staffAssigned\", \"dateMade\", \"roomID\") "
              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, ConfReservation.getResID());
      statement.setString(2, ConfReservation.getStartTime());
      statement.setString(3, ConfReservation.getEndTime());
      statement.setTimestamp(4, ConfReservation.getDateBook());
      statement.setString(5, ConfReservation.getName());
      statement.setString(6, ConfReservation.getUsername());
      statement.setString(7, ConfReservation.getStaffAssigned());
      statement.setTimestamp(8, ConfReservation.getDateMade());
      statement.setInt(9, ConfReservation.getRoomID());

      statement.executeUpdate();

    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
    connection.close();
  }

  /**
   * This method deletes the given ConfReservation object from the database
   *
   * @param ConfReservation the ConfReservation object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void delete(ConfReservation ConfReservation) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query = "DELETE FROM \"ConfReservations\" WHERE \"resID\" = ?";
    try (connection) {

      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, ConfReservation.getResID());

      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    try (Statement statement = connection.createStatement()) {
      ResultSet rs2 = statement.executeQuery(query);
      int count = 0;
      while (rs2.next()) count++;
      if (count == 0) System.out.println("ConfReservation information deleted successfully.");
      else System.out.println("ConfReservation information did not delete.");
    } catch (SQLException e2) {
      System.out.println("Error checking delete. " + e2);
    }
    connection.close();
  }

  /**
   * This method retrieves a ConfReservation object with the specified ID from the
   * "ConfReservations" table in the database.
   *
   * @param rID the ID of the ConfReservation object to retrieve from the "ConfReservations" table
   * @return the ConfReservation object with the specified ID, or null if not found
   * @throws SQLException if there is a problem accessing the database
   */
  public static ConfReservation getConfReservation(int rID) throws SQLException {
    ArrayList<ConfReservation> bookings = new ArrayList<>();
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"ConfReservations\" WHERE \"resID\" = ?";
    ConfReservation ConfReservation = null;
    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, rID);

      ResultSet rs = statement.executeQuery();
      int resID = rs.getInt("resID");
      String startTime = rs.getString("starttime");
      String endTime = rs.getString("endtime");
      Timestamp dateBook = rs.getTimestamp("datebook");
      String name = rs.getString("name");
      String username = rs.getString("dateMade");
      String staff = rs.getString("staffAssigned");
      Timestamp dateMade = rs.getTimestamp("dateMade");
      int roomID = rs.getInt("roomID");
      ConfReservation =
          (new ConfReservation(
              resID, startTime, endTime, dateBook, dateMade, name, username, staff, roomID));
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return ConfReservation;
  }

  /**
   * This method exports all the ConfReservation objects from the "ConfReservation" table in the
   * database to a CSV file at the specified file path.
   *
   * @param csvFilePath the file path of the CSV file to export the ConfReservation objects to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  public static void exportConfReservationsToCSV(String csvFilePath)
      throws SQLException, IOException {
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"ConfReservations\"";
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(query);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
      writer.write("resID,starttime,endtime,dateBook,name,username,staffAssigned,dateMade\n");
      while (resultSet.next()) {
        int resID = resultSet.getInt("resID");
        String startTime = resultSet.getString("starttime");
        String endTime = resultSet.getString("endtime");
        Timestamp dateBook = resultSet.getTimestamp("datebook");
        String name = resultSet.getString("name");
        String username = resultSet.getString("dateMade");
        String staff = resultSet.getString("staffAssigned");
        Timestamp dateMade = resultSet.getTimestamp("dateMade");
        int roomID = resultSet.getInt("roomID");

        String row =
            resID + "," + startTime + "," + endTime + "," + dateBook + "," + name + "," + username
                + "," + staff + ", " + dateMade + "," + roomID + "\n";
        writer.write(row);
      }
      System.out.println("CSV data downloaded from PostgreSQL database");
    } catch (IOException e) {
      System.err.println("Error downloading CSV data from PostgreSQL database: " + e.getMessage());
    }
  }

  /**
   * Uploads CSV data to a PostgreSQL database table "ConfReservations"
   *
   * @param csvFilePath is a String representing a file path
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadConfReservationToPostgreSQL(String csvFilePath)
      throws SQLException, ParseException {
    List<String[]> csvData;
    Connection connection = DataManager.DbConnection();
    DataManager dataImport = new DataManager();
    csvData = dataImport.parseCSVAndUploadToPostgreSQL(csvFilePath);

    try (connection) {
      String query =
          "INSERT INTO \"ConfReservations\" (\"resID\", \"starttime\", \"endtime\", datebook, \"name\", username, \"staffAssigned\", \"dateMade\", \"roomID\") "
              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
      PreparedStatement statement =
          connection.prepareStatement("TRUNCATE TABLE \"ConfReservations\";");
      statement.executeUpdate();
      statement = connection.prepareStatement(query);

      for (int i = 1; i < csvData.size(); i++) {
        String[] row = csvData.get(i);
        statement.setInt(1, Integer.parseInt(row[0])); // resID
        statement.setString(2, row[1]); // startTime
        statement.setString(3, row[2]); // endTime
        statement.setTimestamp(4, Timestamp.valueOf(row[3])); // dateBook
        statement.setString(5, row[4]); // name
        statement.setString(6, row[5]); // username
        statement.setString(7, row[6]); // staff Assigned
        statement.setTimestamp(8, Timestamp.valueOf(row[7])); // dateMade
        statement.setInt(9, Integer.parseInt(row[8]));

        statement.executeUpdate();
      }
      System.out.println("CSV data uploaded to PostgreSQL database");
    } catch (SQLException e) {
      System.err.println("Error uploading CSV data to PostgreSQL database: " + e.getMessage());
    }
  }

  /**
   * * Creates a table for storing ConfReservation data if it doesn't already exist
   *
   * @throws SQLException if connection to the database fails
   */
  public static void createTable() throws SQLException {
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query =
          "create table if not exists \"ConfReservations\"\n"
              + "(\n"
              + "    \"resID\"         integer   not null\n"
              + "        constraint \"ConfReservation_pk\"\n"
              + "            primary key,\n"
              + "    starttime       varchar   not null,\n"
              + "    endtime         varchar   not null,\n"
              + "    datebook        timestamp not null,\n"
              + "    name            varchar,\n"
              + "    username        varchar,\n"
              + "    \"staffAssigned\" varchar,\n"
              + "    \"dateMade\"      timestamp,\n"
              + "    \"roomID\"        integer   not null\n"
              + ");";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
  }
}

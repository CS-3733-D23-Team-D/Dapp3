package edu.wpi.teamname.database;

import edu.wpi.teamname.database.interfaces.ServiceRequestDAO;
import edu.wpi.teamname.servicerequest.RequestType;
import edu.wpi.teamname.servicerequest.ServiceRequest;
import edu.wpi.teamname.servicerequest.Status;
import edu.wpi.teamname.servicerequest.requestitem.RequestItem;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ServiceRequestDAOImpl implements ServiceRequestDAO {
  /**
   * This method updates an existing ServiceRequest object in the "ServiceRequest" table in the
   * database with the new ServiceRequest object.
   *
   * @param serviceRequest the new ServiceRequest object to be updated in the "ServiceRequest" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void sync(ServiceRequest serviceRequest) throws SQLException {
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query =
          "UPDATE \"ServiceRequest\" SET \"roomNum\" = ?, \"staffName\" = ?, \"patientName\" = ?, \"requestedAt\" = ?, \"deliverBy\" = ?, \"status\" = ?, \"requestMadeBy\" = ?, \"requestID\" = ?, \"requestType\" = ?"
              + " WHERE \"requestID\" = ?";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setString(1, serviceRequest.getRoomNumber());
      statement.setString(2, serviceRequest.getStaffName());
      statement.setString(3, serviceRequest.getPatientName());
      statement.setTimestamp(4, serviceRequest.getRequestedAt());
      statement.setTimestamp(5, serviceRequest.getDeliverBy());
      statement.setString(6, serviceRequest.getStatus().getStatusString());
      statement.setString(7, serviceRequest.getRequestMadeBy());
      statement.setInt(8, serviceRequest.getRequestID());
      statement.setString(9, serviceRequest.getRequestType().getString());
      statement.setInt(10, serviceRequest.getOriginalID());
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    connection.close();
  }

  /**
   * The method retrieves all the ServiceRequest objects from the "ServiceRequest" table in the
   * database.
   *
   * @return an ArrayList of the ServiceRequest objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public ArrayList<ServiceRequest> getAll() throws SQLException {
    Connection connection = DataManager.DbConnection();
    ArrayList<ServiceRequest> list = new ArrayList<ServiceRequest>();

    try (connection) {
      String query = "SELECT * FROM \"ServiceRequest\"";
      PreparedStatement statement = connection.prepareStatement(query);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        int requestID = rs.getInt("requestID");
        String roomNum = rs.getString("roomNum");
        String staffName = rs.getString("staffName");
        String patientName = rs.getString("patientName");
        Timestamp requestedAt = rs.getTimestamp("requestedAt");
        Timestamp deliverBy = rs.getTimestamp("deliverBy");
        Status status = Status.valueOf(rs.getString("status"));
        String requestMadeBy = rs.getString("requestMadeBy");
        RequestType requestType = RequestType.valueOf(rs.getString("requestType"));
        list.add(
            new ServiceRequest(
                requestID,
                staffName,
                patientName,
                roomNum,
                deliverBy,
                requestedAt,
                status,
                requestMadeBy,
                requestType));
      }
    }
    connection.close();
    return list;
  }

  public ArrayList<String> getAllIDs() throws SQLException {
    Connection connection = DataManager.DbConnection();
    ArrayList<String> list = new ArrayList<String>();

    try (connection) {
      String query = "SELECT * FROM \"ServiceRequest\"";
      PreparedStatement statement = connection.prepareStatement(query);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        int requestID = rs.getInt("requestID");
        list.add(
            String.valueOf(requestID)
            //                new ServiceRequest(
            //                        requestID,
            //                        staffName,
            //                        patientName,
            //                        roomNum,
            //                        deliverBy,
            //                        requestedAt,
            //                        status,
            //                        requestMadeBy,
            //                        requestType)
            );
      }
    }
    connection.close();
    return list;
  }

  /**
   * Adds a service request to the database, along with the corresponding order of items.
   *
   * @param serviceRequest The service request to be added to the database
   * @throws SQLException If an error occurs while accessing the database
   */
  @Override
  public void add(ServiceRequest serviceRequest) throws SQLException {
    Connection connection = DataManager.DbConnection();
    try {
      String query =
          "INSERT INTO \"ServiceRequest\" (\"requestID\", \"roomNum\", \"staffName\", \"patientName\", \"requestedAt\", \"deliverBy\", \"status\", \"requestMadeBy\", \"requestType\") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, serviceRequest.getRequestID());
      statement.setString(2, serviceRequest.getRoomNumber());
      statement.setString(3, serviceRequest.getStaffName());
      statement.setString(4, serviceRequest.getPatientName());
      statement.setTimestamp(5, serviceRequest.getRequestedAt());
      statement.setTimestamp(6, serviceRequest.getDeliverBy());
      statement.setString(7, serviceRequest.getStatus().getStatusString());
      statement.setString(8, serviceRequest.getRequestMadeBy());
      statement.setString(9, serviceRequest.getRequestType().getString());

      statement.executeUpdate();

      // ItemsOrdered
      ArrayList<RequestItem> items = serviceRequest.getItems();
      // ArrayList<Integer> itemsUsed = new ArrayList<>();
      for (int i = 0; i < items.size(); i++) {
        int newQuantity = getQuantity(serviceRequest.getRequestID(), items.get(i).getItemID()) + 1;
        connection = DataManager.DbConnection();
        // int currID = items.get(i).getItemID();
        // connection;
        // DriverManager.getConnection(
        // DataManager.getDB_URL(), DataManager.getDB_USER(), DataManager.getDB_PASSWORD());
        try {
          if (newQuantity == 1) {
            query =
                "INSERT INTO \"ItemsOrdered\" (\"requestID\", \"itemID\", \"quantity\") "
                    + "VALUES (?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setInt(1, serviceRequest.getRequestID());
            statement.setInt(2, items.get(i).getItemID());
            statement.setInt(3, 1);
            // itemsUsed.add(currID);
          } else {
            query =
                "UPDATE \"ItemsOrdered\" SET quantity = ? WHERE \"itemID\" = ? AND \"requestID\" = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, newQuantity);
            statement.setInt(2, items.get(i).getItemID());
            statement.setInt(3, serviceRequest.getRequestID());
          }
          statement.executeUpdate();

        } catch (SQLException e) {
          System.err.println(e.getMessage());
        }
      }

    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
    connection.close();
  }

  /**
   * Returns the quantity of an item in a service request.
   *
   * @param requestID the ID of the service request
   * @param itemID the ID of the item
   * @return the quantity of the item in the service request
   * @throws SQLException if there is an error accessing the database
   */
  public int getQuantity(int requestID, int itemID) throws SQLException {
    Connection connection = DataManager.DbConnection();

    int quantity = 0;
    try (connection) {
      String query =
          "SELECT \"quantity\" FROM \"ItemsOrdered\" WHERE \"itemID\" = ? AND \"requestID\" = ?";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, itemID);
      statement.setInt(2, requestID);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        quantity = rs.getInt("quantity");
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    System.out.println("quantity: " + quantity);
    return quantity;
    /*if (quantity > 0) {
      return quantity;
    } else {
      return 0;
    }*/
  }

  /**
   * This method deletes the given ServiceRequest object from the database
   *
   * @param serviceRequest the ServiceRequest object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void delete(ServiceRequest serviceRequest) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query = "DELETE FROM \"ServiceRequest\" WHERE \"requestID\" = ?";
    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, serviceRequest.getRequestID());
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    try (Statement statement = connection.createStatement()) {
      ResultSet rs2 = statement.executeQuery(query);
      int count = 0;
      while (rs2.next()) count++;
      if (count == 0) System.out.println("ServiceRequest information deleted successfully.");
      else System.out.println("ServiceRequest information did not delete.");
    } catch (SQLException e2) {
      System.out.println("Error checking delete. " + e2);
    }
    connection.close();
  }

  /**
   * Deletes the ServiceRequest and all associated items from the database.
   *
   * @param serviceRequest The ServiceRequest to delete.
   * @throws SQLException if there is an error executing the SQL query.
   */
  public void deleteWithItems(ServiceRequest serviceRequest) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query = "DELETE FROM \"ServiceRequest\" WHERE \"requestID\" = ?";
    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, serviceRequest.getRequestID());
      statement.executeUpdate();
      query = "DELETE FROM \"ItemsOrdered\" WHERE \"requestID\" = ?";
      statement = connection.prepareStatement(query);
      statement.setInt(1, serviceRequest.getRequestID());
      statement.executeUpdate();

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    try (Statement statement = connection.createStatement()) {
      ResultSet rs2 = statement.executeQuery(query);
      int count = 0;
      while (rs2.next()) count++;
      if (count == 0) System.out.println("ServiceRequest information deleted successfully.");
      else System.out.println("ServiceRequest information did not delete.");
    } catch (SQLException e2) {
      System.out.println("Error checking delete. " + e2);
    }
    connection.close();
  }

  /**
   * This method retrieves an ServiceRequest object with the specified ID from the "ServiceRequest"
   * table in the database.
   *
   * @param id the ID of the ServiceRequest object to retrieve from the "ServiceRequest" table
   * @return the ServiceRequest object with the specified ID, or null if not found
   * @throws SQLException if there is a problem accessing the database
   */
  public static ServiceRequest getServiceRequest(int id) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"ServiceRequest\" WHERE \"requestID\" = ?";
    ServiceRequest serviceRequest = null;
    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, id);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        int rID = rs.getInt("requestID");
        String roomNum = rs.getString("roomNum");
        String staffName = rs.getString("staffName");
        String patientName = rs.getString("patientName");
        Timestamp requestedAt = rs.getTimestamp("requestedAt");
        Timestamp deliverBy = rs.getTimestamp("deliverBy");
        Status status = Status.valueOf(rs.getString("status"));
        String requestMadeBy = rs.getString("requestMadeBy");
        RequestType requestType = RequestType.valueOf(rs.getString("requestType"));
        serviceRequest =
            (new ServiceRequest(
                rID,
                staffName,
                patientName,
                roomNum,
                deliverBy,
                requestedAt,
                status,
                requestMadeBy,
                requestType));
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return serviceRequest;
  }

  /**
   * Updates the staff name for a service request with the given request ID in the database.
   *
   * @param requestID the ID of the service request to update.
   * @param staffName the new staff name to set.
   * @throws SQLException if a database error occurs.
   */
  public static void uploadStaffName(int requestID, String staffName) throws SQLException {
    Connection connection = DataManager.DbConnection();
    try {
      String query = "UPDATE \"ServiceRequest\" SET \"staffName\" = ? WHERE \"requestID\" = ?";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setString(1, staffName);
      statement.setInt(2, requestID);
      statement.executeUpdate();
      connection.close();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Updates the status for a service request with the given request ID in the database.
   *
   * @param requestID the ID of the service request to update.
   * @param status the new staff name to set.
   * @throws SQLException if a database error occurs.
   */
  public static void uploadStatus(int requestID, String status) throws SQLException {
    Connection connection = DataManager.DbConnection();
    try {
      String query = "UPDATE \"ServiceRequest\" SET \"status\" = ? WHERE \"requestID\" = ?";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setString(1, status.toUpperCase());
      statement.setInt(2, requestID);
      statement.executeUpdate();
      connection.close();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
  /**
   * Exports data from a PostgreSQL database table "ServiceRequest" to a CSV file
   *
   * @param csvFilePath a String representing the csv data (must use "//" not "/")
   * @throws SQLException if an error occurs while exporting the data from the database
   * @throws IOException if an error occurs while writing the data to the file
   */
  public static void exportServiceRequestToCSV(String csvFilePath)
      throws SQLException, IOException {
    ServiceRequestDAOImpl serviceRequestDAO = new ServiceRequestDAOImpl();
    ArrayList<ServiceRequest> serviceRequests = serviceRequestDAO.getAll();

    FileWriter writer = new FileWriter(csvFilePath);
    writer.write(
        "Request ID,Staff Name,Patient Name,Room Number,Requested At,Deliver By,Status,Request Made By\n");

    for (ServiceRequest sr : serviceRequests) {
      writer.write(
          sr.getRequestID()
              + ","
              + sr.getStaffName()
              + ","
              + sr.getPatientName()
              + ","
              + sr.getRoomNumber()
              + ","
              + sr.getRequestedAt()
              + ","
              + sr.getDeliverBy()
              + ","
              + sr.getStatus().getStatusString()
              + ","
              + sr.getRequestMadeBy()
              + ","
              + sr.getRequestType().getString()
              + "\n");
    }
    writer.close();
  }
  /**
   * Uploads CSV data to a PostgreSQL database table "ServiceRequest"
   *
   * @param csvFilePath is a String representing the filepath of the file we want to upload (use"\\"
   *     instead of "\")
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadServiceRequestToPostgreSQL(String csvFilePath) throws SQLException {
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
                "CREATE TABLE \"ServiceRequest\" (\"requestID\" INTEGER NOT NULL, \"roomNum\" VARCHAR(255) NOT NULL, \"staffName\" VARCHAR(255) NOT NULL, \"patientName\" VARCHAR(255) NOT NULL, \"requestedAt\" TIMESTAMP NOT NULL, \"deliverBy\" TIMESTAMP NOT NULL, \"status\" VARCHAR(255) NOT NULL, \"requestMadeBy\" VARCHAR(255), \"requestType\" VARCHAR(255), PRIMARY KEY (\"requestID\"))");
        createTableStatement.executeUpdate();
      }

      String query =
          "INSERT INTO \"ServiceRequest\" (\"requestID\", \"roomNum\", \"staffName\", \"patientName\", \"requestedAt\", \"deliverBy\", \"status\", \"requestMadeBy\", \"requestType\") "
              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
      PreparedStatement statement =
          connection.prepareStatement("TRUNCATE TABLE \"ServiceRequest\";");
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
        statement.setString(2, row[3]); // Swap roomNum and patientName columns
        statement.setString(3, row[1]);
        statement.setString(4, row[2]);

        // Parse date columns only if they are not "BLANK"
        if (!"BLANK".equals(row[4])) {
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
          java.util.Date parsedDate = dateFormat.parse(row[4]);
          java.sql.Date date = new java.sql.Date(parsedDate.getTime());
          statement.setDate(5, date);
        } else {
          statement.setNull(5, Types.DATE);
        }

        if (!"BLANK".equals(row[5])) {
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
          java.util.Date parsedDate = dateFormat.parse(row[5]);
          java.sql.Date date = new java.sql.Date(parsedDate.getTime());
          statement.setDate(6, date);
        } else {
          statement.setNull(6, Types.DATE);
        }

        // Assign default value "N/A" to status column if it is missing
        if (row[6] == null || row[6].isEmpty()) {
          statement.setString(7, "N/A");
        } else {
          statement.setString(7, row[6]);
        }

        statement.setString(8, row[7]);
        statement.setString(9, row[8]);

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

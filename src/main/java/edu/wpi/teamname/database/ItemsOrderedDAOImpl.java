package edu.wpi.teamname.database;

import edu.wpi.teamname.database.interfaces.ItemsOrderedDAO;
import edu.wpi.teamname.servicerequest.ItemsOrdered;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemsOrderedDAOImpl implements ItemsOrderedDAO {
  /**
   * This method updates an existing ItemsOrdered object in the "ItemsOrdered" table in the database
   * with the new ItemsOrdered object.
   *
   * @param itemsOrdered the new ItemsOrdered object to be updated in the "ItemsOrdered" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void sync(ItemsOrdered itemsOrdered) throws SQLException {
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query =
          "UPDATE \"ItemsOrdered\" SET \"requestID\" = ?, \"itemID\" = ?, \"quantity\" = ?"
              + " WHERE \"requestID\" = ? AND \"itemID\" = ?";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, itemsOrdered.getRequestID());
      statement.setInt(2, itemsOrdered.getItemID());
      statement.setInt(3, itemsOrdered.getQuantity());
      statement.setInt(4, itemsOrdered.getRequestID());
      statement.setInt(5, itemsOrdered.getItemID());
      ;
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    connection.close();
  }

  /**
   * The method retrieves all the ItemsOrdered objects from the "ItemsOrdered" table in the
   * database.
   *
   * @return an ArrayList of the ItemsOrdered objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public ArrayList<ItemsOrdered> getAll() throws SQLException {
    Connection connection = DataManager.DbConnection();
    ArrayList<ItemsOrdered> list = new ArrayList<ItemsOrdered>();

    try (connection) {
      String query = "SELECT * FROM \"ItemsOrdered\"";
      PreparedStatement statement = connection.prepareStatement(query);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        int requestID = rs.getInt("requestID");
        int itemID = rs.getInt("itemID");
        int quantity = rs.getInt("quantity");
        list.add(new ItemsOrdered(requestID, itemID, quantity));
      }
    }
    connection.close();
    return list;
  }

  /**
   * This method adds a new ItemsOrdered object to the "ItemsOrdered" table in the database.
   *
   * @param itemsOrdered the ItemsOrdered object to be added to the "ItemsOrdered" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void add(ItemsOrdered itemsOrdered) throws SQLException {
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query =
          "INSERT INTO \"ItemsOrdered\" (\"requestID\", \"itemID\", \"quantity\") "
              + "VALUES (?, ?, ?)";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, itemsOrdered.getRequestID());
      statement.setInt(2, itemsOrdered.getItemID());
      statement.setInt(3, itemsOrdered.getQuantity());

      statement.executeUpdate();

    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
    connection.close();
  }

  /**
   * This method deletes the given ItemsOrdered object from the database
   *
   * @param itemsOrdered the ItemsOrdered object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void delete(ItemsOrdered itemsOrdered) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query = "DELETE FROM \"ItemsOrdered\" WHERE \"requestID\" = ? AND \"itemID\" = ?";
    try (connection) {

      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, itemsOrdered.getRequestID());
      statement.setInt(2, itemsOrdered.getItemID());

      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    try (Statement statement = connection.createStatement()) {
      ResultSet rs2 = statement.executeQuery(query);
      int count = 0;
      while (rs2.next()) count++;
      if (count == 0) System.out.println("ItemOrdered information deleted successfully.");
      else System.out.println("ItemOrdered information did not delete.");
    } catch (SQLException e2) {
      System.out.println("Error checking delete. " + e2);
    }
    connection.close();
  }

  /**
   * This method retrieves an ItemsOrdered object with the specified ID from the "ItemsOrdered"
   * table in the database.
   *
   * @param requestID the requestID of the ItemsOrdered object to retrieve from the "ItemsOrdered"
   *     table
   * @param itemID the itemID of the ItemsOrdered object to retrieve from the "ItemsOrdered" table
   * @return the ItemsOrdered object that matches the specified ID's, or null if not found
   * @throws SQLException if there is a problem accessing the database
   */
  public static ItemsOrdered getItemOrdered(int requestID, int itemID) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"ItemsOrdered\" WHERE \"requestID\" = ? AND \"itemID\" = ?";
    ItemsOrdered itemsOrdered = null;
    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, requestID);
      statement.setInt(2, itemID);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        int rID = rs.getInt("requestID");
        int iID = rs.getInt("itemID");
        int quantity = rs.getInt("quantity");
        itemsOrdered = (new ItemsOrdered(rID, iID, quantity));
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return itemsOrdered;
  }

  /**
   * This method exports all the ItemsOrdered objects from the "ItemsOrdered" table in the database
   * to a CSV file at the specified file path.
   *
   * @param csvFilePath the file path of the CSV file to export the ItemsOrdered objects to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  public static void exportItemsOrderedToCSV(String csvFilePath) throws SQLException, IOException {
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"ItemsOrdered\"";
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(query);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
      writer.write("requestID,itemID,quantity\n");
      while (resultSet.next()) {
        int requestID = resultSet.getInt("requestID");
        int itemID = resultSet.getInt("itemID");
        int quantity = resultSet.getInt("quantity");

        String row = requestID + "," + itemID + "," + quantity + "\n";
        writer.write(row);
      }
      System.out.println("CSV data downloaded from PostgreSQL database");
    } catch (IOException e) {
      System.err.println("Error downloading CSV data from PostgreSQL database: " + e.getMessage());
    }
  }

  /**
   * Uploads CSV data to a PostgreSQL database table "ItemsOrdered"
   *
   * @param csvFilePath is a String representing a file path
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadItemsOrderedToPostgreSQL(String csvFilePath) throws SQLException {
    List<String[]> csvData;
    Connection connection = DataManager.DbConnection();
    DataManager dataImport = new DataManager();
    csvData = dataImport.parseCSVAndUploadToPostgreSQL(csvFilePath);

    try (connection) {
      String query =
          "INSERT INTO \"ItemsOrdered\" (\"requestID\", \"itemID\", \"quantity\") VALUES (?, ?, ?)";
      PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE \"ItemsOrdered\";");
      statement.executeUpdate();
      statement = connection.prepareStatement(query);

      for (int i = 1; i < csvData.size(); i++) {
        String[] row = csvData.get(i);
        statement.setInt(1, Integer.parseInt(row[0])); // request is an int column
        statement.setInt(2, Integer.parseInt(row[1])); // itemID is an int column
        statement.setInt(3, Integer.parseInt(row[2])); // quantity is an int column

        statement.executeUpdate();
      }
      System.out.println("CSV data uploaded to PostgreSQL database");
    } catch (SQLException e) {
      System.err.println("Error uploading CSV data to PostgreSQL database: " + e.getMessage());
    }
  }
}

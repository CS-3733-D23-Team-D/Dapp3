package edu.wpi.teamname.database;

import edu.wpi.teamname.database.interfaces.OfficeSupplyDAO;
import edu.wpi.teamname.servicerequest.requestitem.OfficeSupply;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class OfficeSupplyDAOImpl implements OfficeSupplyDAO {
  /**
   * This method updates an existing OfficeSupply object in the "OfficeSupply" table in the database
   * with the new OfficeSupply object.
   *
   * @param officeSupply the new OfficeSupply object to be updated in the "OfficeSupply" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void sync(OfficeSupply officeSupply) throws SQLException {
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query =
          "UPDATE \"OfficeSupply\" SET \"supplyID\" = ?, \"name\" = ?, \"price\" = ?, \"category\" = ?, \"isElectric\" = ?"
              + " WHERE \"supplyID\" = ?";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, officeSupply.getItemID());
      statement.setString(2, officeSupply.getName());
      statement.setFloat(3, officeSupply.getPrice());
      statement.setString(4, officeSupply.getCategory());
      statement.setBoolean(5, officeSupply.isElectric());
      statement.setInt(6, officeSupply.getOriginalID());

      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    connection.close();
  }

  /**
   * The method retrieves all the OfficeSupply objects from the "OfficeSupply" table in the
   * database.
   *
   * @return an ArrayList of the OfficeSupply objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public ArrayList<OfficeSupply> getAll() throws SQLException {
    Connection connection = DataManager.DbConnection();
    ArrayList<OfficeSupply> list = new ArrayList<OfficeSupply>();

    try (connection) {
      String query = "SELECT * FROM \"OfficeSupply\"";
      PreparedStatement statement = connection.prepareStatement(query);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        int id = rs.getInt("supplyID");
        String name = rs.getString("name");
        float price = rs.getFloat("price");
        String category = rs.getString("category");
        Boolean color = rs.getBoolean("isElectric");
        list.add(new OfficeSupply(id, name, price, category, color));
      }
    }
    connection.close();
    return list;
  }

  /**
   * This method adds a new OfficeSupply object to the "OfficeSupply" table in the database.
   *
   * @param officeSupply the OfficeSupply object to be added to the "OfficeSupply" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void add(OfficeSupply officeSupply) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query =
        "INSERT INTO \"OfficeSupply\" (\"supplyID\", \"name\", \"price\", \"category\", \"isElectric\") "
            + "VALUES (?, ?, ?, ?, ?)";

    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, officeSupply.getItemID());
      statement.setString(2, officeSupply.getName());
      statement.setFloat(3, officeSupply.getPrice());
      statement.setString(4, officeSupply.getCategory());
      statement.setBoolean(5, officeSupply.isElectric());
      statement.executeUpdate();
      System.out.println("Office Supply information has been successfully added to the database.");
    } catch (SQLException e) {
      System.err.println("Error adding Office Supply information to database: " + e.getMessage());
    }
  }

  /**
   * This method deletes the given OfficeSupply object from the database
   *
   * @param officeSupply the OfficeSupply object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void delete(OfficeSupply officeSupply) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query = "Delete from \"OfficeSupply\" where \"supplyID\" = ?";

    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setInt(1, officeSupply.getItemID());
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Delete in OfficeSupply table error. " + e);
    }

    try (Statement statement = connection.createStatement()) {
      ResultSet rs2 = statement.executeQuery(query);
      int count = 0;
      while (rs2.next()) count++;
      if (count == 0) System.out.println("OfficeSupply information deleted successfully.");
      else System.out.println("OfficeSupply information did not delete.");
    } catch (SQLException e2) {
      System.out.println("Error checking delete. " + e2);
    }
  }

  /**
   * This method retrieves an OfficeSupply object with the specified ID from the "OfficeSupply"
   * table in the database.
   *
   * @param id the ID of the OfficeSupply object to retrieve from the "OfficeSupply" table
   * @return the Flower object with the specified ID, or null if not found
   * @throws SQLException if there is a problem accessing the database
   */
  public static OfficeSupply getOfficeSupply(int id) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"OfficeSupply\" WHERE \"supplyID\" = ?";
    OfficeSupply officeSupply = null;
    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, id);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        int mealid = rs.getInt("supplyID");
        String name = rs.getString("name");
        float price = rs.getFloat("price");
        String category = rs.getString("category");
        Boolean isElectric = rs.getBoolean("isElectric");
        officeSupply = (new OfficeSupply(mealid, name, price, category, isElectric));
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return officeSupply;
  }

  /**
   * Exports data from a PostgreSQL database table "OfficeSupply" to a CSV file
   *
   * @param csvFilePath is a String representing a file path
   * @throws SQLException if an error occurs while exporting the data from the database
   */
  public static void exportOfficeSupplyToCSV(String csvFilePath) throws SQLException, IOException {
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"OfficeSupply\"";
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(query);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
      writer.write("supplyID,name,price,category,isElectric\n");
      while (resultSet.next()) {
        int id = resultSet.getInt("supplyID");
        String name = resultSet.getString("name");
        int price = resultSet.getInt("price");
        String category = resultSet.getString("category");
        Boolean electric = resultSet.getBoolean("isElectric");
        String row = id + "," + name + "," + price + "," + category + "," + electric + "\n";
        writer.write(row);
      }
      System.out.println("CSV data downloaded from PostgreSQL database");
    } catch (IOException e) {
      System.err.println("Error downloading CSV data from PostgreSQL database: " + e.getMessage());
    }
  }

  /**
   * Uploads CSV data to a PostgreSQL database table "OfficeSupply"
   *
   * @param csvFilePath is a String representing a file path
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadOfficeSupplyToPostgreSQL(String csvFilePath)
      throws SQLException, ParseException {
    List<String[]> csvData;
    Connection connection = DataManager.DbConnection();
    DataManager dataImport = new DataManager();
    csvData = dataImport.parseCSVAndUploadToPostgreSQL(csvFilePath);

    try (connection) {
      String query =
          "INSERT INTO \"OfficeSupply\" (\"supplyID\", \"name\", \"price\", \"category\", \"isElectric\") VALUES (?, ?, ?, ?, ?)";
      PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE \"OfficeSupply\";");
      statement.executeUpdate();
      statement = connection.prepareStatement(query);

      for (int i = 1; i < csvData.size(); i++) {
        String[] row = csvData.get(i);
        statement.setInt(1, Integer.parseInt(row[0])); // furnitureID is an int column
        statement.setString(2, row[1]); // name is a string column
        statement.setFloat(3, Float.parseFloat(row[2])); // price is a float column
        statement.setString(4, row[3]); // category is a string column
        statement.setBoolean(5, Boolean.parseBoolean(row[4])); // isElectric is a boolean column

        statement.executeUpdate();
      }
      System.out.println("CSV data uploaded to PostgreSQL database");
    } catch (SQLException e) {
      System.err.println("Error uploading CSV data to PostgreSQL database: " + e.getMessage());
    }
  }
}

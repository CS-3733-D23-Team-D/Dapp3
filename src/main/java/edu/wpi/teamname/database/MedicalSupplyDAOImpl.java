package edu.wpi.teamname.database;

import edu.wpi.teamname.servicerequest.requestitem.MedicalSupply;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MedicalSupplyDAOImpl {
  /**
   * This method updates an existing MedicalSupply object in the "MedicalSupply" table in the
   * database with the new MedicalSupply object.
   *
   * @param MedicalSupply the new MedicalSupply object to be updated in the "MedicalSupply" table
   * @throws SQLException if there is a problem accessing the database
   */
  // @Override
  public void sync(MedicalSupply MedicalSupply) throws SQLException {
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query =
          "UPDATE \"MedicalSupplies\" SET \"supplyID\" = ?, \"Name\" = ?, \"Price\" = ?, \"Type\" = ?, \"AccessLevel\" = ?"
              + " WHERE \"supplyID\" = ?";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, MedicalSupply.getItemID());
      statement.setString(2, MedicalSupply.getName());
      statement.setFloat(3, MedicalSupply.getPrice());
      statement.setString(4, MedicalSupply.getType());
      statement.setInt(5, MedicalSupply.getAccessLvl());

      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    connection.close();
  }

  /**
   * The method retrieves all the MedicalSupply objects from the "MedicalSupply" table in the
   * database.
   *
   * @return an ArrayList of the MedicalSupply objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  // @Override
  public ArrayList<MedicalSupply> getAll() throws SQLException {
    Connection connection = DataManager.DbConnection();
    ArrayList<MedicalSupply> list = new ArrayList<MedicalSupply>();

    try (connection) {
      String query = "SELECT * FROM \"MedicalSupplies\"";
      PreparedStatement statement = connection.prepareStatement(query);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        int id = rs.getInt("supplyID");
        String name = rs.getString("Name");
        float price = rs.getFloat("Price");
        String type = rs.getString("Type");
        int accessLvl = rs.getInt("AccessLevel");
        list.add(new MedicalSupply(id, name, price, type, accessLvl));
      }
    }
    connection.close();
    return list;
  }

  /**
   * This method adds a new MedicalSupply object to the "MedicalSupply" table in the database.
   *
   * @param MedicalSupply the MedicalSupply object to be added to the "MedicalSupply" table
   * @throws SQLException if there is a problem accessing the database
   */
  // @Override
  public void add(MedicalSupply MedicalSupply) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query =
        "INSERT INTO \"MedicalSupplies\" (\"supplyID\", \"name\", \"price\", \"category\", \"isElectric\") "
            + "VALUES (?, ?, ?, ?, ?)";

    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, MedicalSupply.getItemID());
      statement.setString(2, MedicalSupply.getName());
      statement.setFloat(3, MedicalSupply.getPrice());
      statement.setString(4, MedicalSupply.getType());
      statement.setInt(5, MedicalSupply.getAccessLvl());
      statement.executeUpdate();
      System.out.println("Medical Supply information has been successfully added to the database.");
    } catch (SQLException e) {
      System.err.println("Error adding Medical Supply information to database: " + e.getMessage());
    }
  }

  /**
   * This method deletes the given MedicalSupply object from the database
   *
   * @param MedicalSupply the MedicalSupply object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  // @Override
  public void delete(MedicalSupply MedicalSupply) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query = "Delete from \"MedicalSupplies\" where \"supplyID\" = ?";

    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setInt(1, MedicalSupply.getItemID());
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Delete in MedicalSupply table error. " + e);
    }

    try (Statement statement = connection.createStatement()) {
      ResultSet rs2 = statement.executeQuery(query);
      int count = 0;
      while (rs2.next()) count++;
      if (count == 0) System.out.println("MedicalSupply information deleted successfully.");
      else System.out.println("MedicalSupply information did not delete.");
    } catch (SQLException e2) {
      System.out.println("Error checking delete. " + e2);
    }
  }

  /**
   * This method retrieves an MedicalSupply object with the specified ID from the "MedicalSupply"
   * table in the database.
   *
   * @param id the ID of the MedicalSupply object to retrieve from the "MedicalSupply" table
   * @return the Flower object with the specified ID, or null if not found
   * @throws SQLException if there is a problem accessing the database
   */
  public static MedicalSupply getMedicalSupply(int id) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"MedicalSupplies\" WHERE \"supplyID\" = ?";
    MedicalSupply MedicalSupply = null;
    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, id);
      ResultSet rs = statement.executeQuery();
      rs.next();
      int supid = rs.getInt("supplyID");
      String name = rs.getString("Name");
      float price = rs.getFloat("Price");
      String type = rs.getString("Type");
      int accessLvl = rs.getInt("AccessLevel");
      MedicalSupply = (new MedicalSupply(supid, name, price, type, accessLvl));
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return MedicalSupply;
  }

  /**
   * Exports data from a PostgreSQL database table "MedicalSupply" to a CSV file
   *
   * @param csvFilePath is a String representing a file path
   * @throws SQLException if an error occurs while exporting the data from the database
   */
  public static void exportMedicalSupplyToCSV(String csvFilePath) throws SQLException, IOException {
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"MedicalSupplies\"";
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(query);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
      writer.write("supplyID,Name,Price,Type,AccessLevel\n");
      while (resultSet.next()) {
        int id = resultSet.getInt("supplyID");
        String name = resultSet.getString("Name");
        int price = resultSet.getInt("Price");
        String type = resultSet.getString("Type");
        int accessLvl = resultSet.getInt("AccessLevel");
        String row = id + "," + name + "," + price + "," + type + "," + accessLvl + "\n";
        writer.write(row);
      }
      System.out.println("CSV data downloaded from PostgreSQL database");
    } catch (IOException e) {
      System.err.println("Error downloading CSV data from PostgreSQL database: " + e.getMessage());
    }
  }

  /**
   * Uploads CSV data to a PostgreSQL database table "MedicalSupply"
   *
   * @param csvFilePath is a String representing a file path
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadMedicalSupplyToPostgreSQL(String csvFilePath)
      throws SQLException, ParseException {
    List<String[]> csvData;
    Connection connection = DataManager.DbConnection();
    DataManager dataImport = new DataManager();
    csvData = dataImport.parseCSVAndUploadToPostgreSQL(csvFilePath);

    try (connection) {
      String query =
          "INSERT INTO \"MedicalSupplies\" (\"supplyID\", \"Name\", \"Price\", \"Type\", \"AccessLevel\") VALUES (?, ?, ?, ?, ?)";
      PreparedStatement statement =
          connection.prepareStatement("TRUNCATE TABLE \"MedicalSupplies\";");
      statement.executeUpdate();
      statement = connection.prepareStatement(query);

      for (int i = 1; i < csvData.size(); i++) {
        String[] row = csvData.get(i);
        statement.setInt(1, Integer.parseInt(row[0])); // supplyID is an int column
        statement.setString(2, row[1]); // name is a string column
        statement.setFloat(3, Float.parseFloat(row[2])); // price is a float column
        statement.setString(4, row[3]); // type is a string column
        statement.setInt(5, Integer.parseInt(row[4])); // AccessLevel is a int column

        statement.executeUpdate();
      }
      System.out.println("CSV data uploaded to PostgreSQL database");
    } catch (SQLException e) {
      System.err.println("Error uploading CSV data to PostgreSQL database: " + e.getMessage());
    }
  }
}

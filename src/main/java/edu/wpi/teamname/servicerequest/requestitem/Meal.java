package edu.wpi.teamname.servicerequest.requestitem;

import edu.wpi.teamname.database.DataManager;
import java.sql.*;
import lombok.Getter;
import lombok.Setter;

public class Meal extends RequestItem {
  @Setter @Getter private String meal;
  @Setter @Getter private String cuisine;

  /**
   * creates a meal item
   *
   * @param itemID id of the item
   * @param name name of the meal item
   * @param price price of the meal
   * @param meal meal dinner (breakfast, lunch, dinner)
   * @param cuisine type of cuisine of the meal
   */
  public Meal(int itemID, String name, float price, String meal, String cuisine) {
    super(itemID, name, price);
    this.meal = meal;
    this.cuisine = cuisine;
  }

  /**
   * Creates a meal object only using the ID and gets the rest of the data from the database
   *
   * @param mealID id of the meal
   * @throws SQLException thrown when there is an error connecting to the database or an error with
   *     the sql query syntax
   */
  public Meal(int mealID) throws SQLException {
    super(mealID);
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"Meal\" WHERE \"mealID\" = ?;";

    String name = null;
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setInt(1, mealID);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        super.setItemID(mealID);
        super.setName(rs.getString("Name"));
        setPrice(rs.getFloat("Price"));
        setMeal(rs.getString("Meal"));
        setCuisine(rs.getString("Cuisine"));
      }
    } catch (SQLException e) {
      System.out.println("Error retrieving meal data: " + e.getMessage());
    }
  }

  /**
   * a toString method for the meal items
   *
   * @return String in format [ <itemID>, <name>, <price>, <meal>, <cuisine>]
   */
  public String toString() {
    return "["
        + this.getItemID()
        + ", "
        + this.getName()
        + ", "
        + this.getPrice()
        + ", "
        + meal
        + ", "
        + cuisine
        + "]";
  }
}

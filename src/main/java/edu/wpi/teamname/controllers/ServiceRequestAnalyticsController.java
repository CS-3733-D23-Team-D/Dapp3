package edu.wpi.teamname.controllers;

import edu.wpi.teamname.ThemeSwitch;
import edu.wpi.teamname.database.*;
import edu.wpi.teamname.servicerequest.ItemsOrdered;
import edu.wpi.teamname.servicerequest.RequestType;
import edu.wpi.teamname.servicerequest.ServiceRequest;
import edu.wpi.teamname.servicerequest.Status;
import edu.wpi.teamname.servicerequest.requestitem.Flower;
import edu.wpi.teamname.servicerequest.requestitem.Meal;
import edu.wpi.teamname.servicerequest.requestitem.MedicalSupply;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;

/**
 * The ServiceRequestAnalyticsController class is responsible for populating and managing the
 * Service Request Analytics view.
 */
public class ServiceRequestAnalyticsController {
  @FXML AnchorPane root;
  @FXML private BarChart<String, Number> barChart;
  @FXML private PieChart pieChart;

  @FXML private ComboBox piechartCombo;
  @FXML private ComboBox barChartCombo;
  // Declare count variables at the class level
  private Map<RequestType, Integer> requestTypeCounts;
  private Map<Status, Integer> statusCounts;
  private Map<String, Integer> requesterCounts;
  private Map<LocalDate, Integer> dailyRequestCounts;
  private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  // bar chart list: Service Requests Per Day, Service Requests By Type
  // pie chart list:Service Requests By Status
  @FXML
  private void initialize() {
    ThemeSwitch.switchTheme(root);

    barChartCombo
        .getItems()
        .addAll(
            "Service Requests Per Day",
            "Price By Flower",
            "Service Requests By Type",
            "Flowers Ordered By Category",
            "Medical Supplies By Type",
            "Meals By Cuisine");

    piechartCombo.getItems().addAll("Service Requests By Status", "Flowers By Color");

    barChartCombo
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            new ChangeListener<String>() {
              @Override
              public void changed(
                  ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateBarChart(newValue);
              }
            });

    piechartCombo
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            new ChangeListener<String>() {
              @Override
              public void changed(
                  ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updatePieChart(newValue);
              }
            });

    populateCharts();
  }
  /** Updates the BarChart based on the selected option in the ComboBox. */
  private void updateBarChart(String selectedOption) {
    if (selectedOption == null) return;

    barChart.getData().clear();

    XYChart.Series<String, Number> barChartData = new XYChart.Series<>();

    switch (selectedOption) {
      case "Service Requests Per Day":
        for (Map.Entry<LocalDate, Integer> entry : dailyRequestCounts.entrySet()) {
          barChartData
              .getData()
              .add(new XYChart.Data<>(entry.getKey().format(dateFormatter), entry.getValue()));
        }
        break;
      case "Flowers Ordered By Category":
        try {
          Map<String, Integer> categoryCounts = getFlowersOrderedByCategory();
          for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            barChartData.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
          }
        } catch (SQLException e) {
          e.printStackTrace();
        }
        break;
      case "Service Requests By Type":
        for (Map.Entry<RequestType, Integer> entry : requestTypeCounts.entrySet()) {
          barChartData
              .getData()
              .add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }
        break;
      case "Price By Flower":
        try {
          Map<String, Float> priceByFlower = getPriceByFlower();
          for (Map.Entry<String, Float> entry : priceByFlower.entrySet()) {
            barChartData.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
          }
        } catch (SQLException e) {
          e.printStackTrace();
        }
        break;
      case "Medical Supplies By Type":
        try {
          Map<String, Integer> suppliesByType = getMedicalSuppliesByType();
          for (Map.Entry<String, Integer> entry : suppliesByType.entrySet()) {
            barChartData.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
          }
        } catch (SQLException e) {
          e.printStackTrace();
        }
        break;
      case "Meals By Cuisine":
        try {
          Map<String, Integer> mealsByCuisine = getMealsByCuisine();
          for (Map.Entry<String, Integer> entry : mealsByCuisine.entrySet()) {
            barChartData.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
          }
        } catch (SQLException e) {
          e.printStackTrace();
        }
        break;
    }

    barChart.getData().add(barChartData);
  }

  private Map<String, Integer> getFlowersByColor() throws SQLException {
    FlowerDAOImpl flowerDAO = new FlowerDAOImpl();
    List<Flower> flowers = flowerDAO.getAll();

    Map<String, Integer> colorCounts = new HashMap<>();

    for (Flower flower : flowers) {
      String color = flower.getColor();
      colorCounts.put(color, colorCounts.getOrDefault(color, 0) + 1);
    }

    return colorCounts;
  }

  private Map<String, Integer> getMealsByCuisine() throws SQLException {
    MealDAOImpl mealDAO = new MealDAOImpl();
    List<Meal> meals = mealDAO.getAll();

    Map<String, Integer> mealsByCuisine = new HashMap<>();

    for (Meal meal : meals) {
      String cuisine = meal.getCuisine();
      mealsByCuisine.put(cuisine, mealsByCuisine.getOrDefault(cuisine, 0) + 1);
    }

    return mealsByCuisine;
  }

  /** Updates the PieChart based on the selected option in the ComboBox. */
  private void updatePieChart(String selectedOption) {
    if (selectedOption == null) return;

    pieChart.getData().clear();

    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

    switch (selectedOption) {
      case "Service Requests By Status":
        for (Map.Entry<Status, Integer> entry : statusCounts.entrySet()) {
          pieChartData.add(new PieChart.Data(entry.getKey().toString(), entry.getValue()));
        }
        break;
      case "Flowers By Color":
        try {
          Map<String, Integer> colorCounts = getFlowersByColor();
          for (Map.Entry<String, Integer> entry : colorCounts.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
          }
        } catch (SQLException e) {
          e.printStackTrace();
        }
        break;
    }

    pieChart.setData(pieChartData);
  }

  private Map<String, Integer> getFlowersOrderedByCategory() throws SQLException {
    ItemsOrderedDAOImpl itemsOrderedDAO = new ItemsOrderedDAOImpl();
    FlowerDAOImpl flowerDAO = new FlowerDAOImpl();
    List<ItemsOrdered> itemsOrdered = itemsOrderedDAO.getAll();
    List<Flower> flowers = flowerDAO.getAll();

    Map<Integer, Flower> flowerMap =
        flowers.stream().collect(Collectors.toMap(Flower::getItemID, Function.identity()));
    Map<String, Integer> categoryCounts = new HashMap<>();

    for (ItemsOrdered item : itemsOrdered) {
      Flower flower = flowerMap.get(item.getItemID());
      if (flower != null) {
        String category = flower.getCategory();
        categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + item.getQuantity());
      }
    }

    return categoryCounts;
  }

  private Map<String, Float> getPriceByFlower() throws SQLException {
    FlowerDAOImpl flowerDAO = new FlowerDAOImpl();
    List<Flower> flowers = flowerDAO.getAll();

    Map<String, Float> priceByFlower = new HashMap<>();

    for (Flower flower : flowers) {
      String name = flower.getName();
      float price = flower.getPrice();
      priceByFlower.put(name, price);
    }

    return priceByFlower;
  }

  /** Populates the charts with data obtained from the database. */
  public void populateCharts() {
    ServiceRequestDAOImpl serviceRequestDAO = new ServiceRequestDAOImpl();
    List<ServiceRequest> serviceRequests = new ArrayList<>();

    try {
      serviceRequests = serviceRequestDAO.getAll();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    ParentController.titleString.set("Service Request Analytics");

    requestTypeCounts = new HashMap<>();
    statusCounts = new HashMap<>();
    requesterCounts = new HashMap<>();
    dailyRequestCounts = new HashMap<>(); // Add this line

    for (ServiceRequest request : serviceRequests) {
      requestTypeCounts.merge(request.getRequestType(), 1, Integer::sum);
      statusCounts.merge(request.getStatus(), 1, Integer::sum);
      requesterCounts.merge(request.getRequestMadeBy(), 1, Integer::sum);
      LocalDate date = request.getRequestedAt().toLocalDateTime().toLocalDate();
      dailyRequestCounts.put(date, dailyRequestCounts.getOrDefault(date, 0) + 1);
    }

    // Call update methods to populate the charts
    updateBarChart((String) barChartCombo.getSelectionModel().getSelectedItem());
    updatePieChart((String) piechartCombo.getSelectionModel().getSelectedItem());
  }

  private Map<String, Integer> getMedicalSuppliesByType() throws SQLException {
    MedicalSupplyDAOImpl medicalSupplyDAO = new MedicalSupplyDAOImpl();
    List<MedicalSupply> medicalSupplies = medicalSupplyDAO.getAll();

    Map<String, Integer> suppliesByType = new HashMap<>();

    for (MedicalSupply supply : medicalSupplies) {
      String type = supply.getType();
      suppliesByType.put(type, suppliesByType.getOrDefault(type, 0) + 1);
    }

    return suppliesByType;
  }
  //  private String getMostPopularMeal() throws SQLException {
  //    MealDAOImpl mealDAO = new MealDAOImpl();
  //    List<Meal> meals = mealDAO.getAll();
  //
  //    Map<String, Integer> mealCounts = new HashMap<>();
  //
  //    for (Meal meal : meals) {
  //      String mealName = meal.getName();
  //      mealCounts.put(mealName, mealCounts.getOrDefault(mealName, 0) + 1);
  //    }
  //
  //    String mostPopularMeal = null;
  //    int maxCount = 0;
  //
  //    for (Map.Entry<String, Integer> entry : mealCounts.entrySet()) {
  //      if (entry.getValue() > maxCount) {
  //        mostPopularMeal = entry.getKey();
  //        maxCount = entry.getValue();
  //      }
  //    }
  //
  //    return mostPopularMeal;
  //  }

}

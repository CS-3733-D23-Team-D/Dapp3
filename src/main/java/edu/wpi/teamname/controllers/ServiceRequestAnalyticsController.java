package edu.wpi.teamname.controllers;

import edu.wpi.teamname.database.ServiceRequestDAOImpl;
import edu.wpi.teamname.servicerequest.RequestType;
import edu.wpi.teamname.servicerequest.ServiceRequest;
import edu.wpi.teamname.servicerequest.Status;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;

/**
 * The ServiceRequestAnalyticsController class is responsible for populating and managing the
 * Service Request Analytics view.
 */
public class ServiceRequestAnalyticsController {
  @FXML private BarChart<String, Number> barChart;
  @FXML private PieChart pieChart;
  @FXML private BarChart<String, Number> barChart2;

  @FXML private NumberAxis yAxis; // Change this line

  @FXML
  private void initialize() {
    populateCharts();
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

    Map<RequestType, Integer> requestTypeCounts = new HashMap<>();
    Map<Status, Integer> statusCounts = new HashMap<>();
    Map<String, Integer> requesterCounts = new HashMap<>();
    Map<LocalDate, Integer> dailyRequestCounts = new HashMap<>(); // Add this line

    for (ServiceRequest request : serviceRequests) {
      requestTypeCounts.merge(request.getRequestType(), 1, Integer::sum);
      statusCounts.merge(request.getStatus(), 1, Integer::sum);
      requesterCounts.merge(request.getRequestMadeBy(), 1, Integer::sum);
      LocalDate date = request.getRequestedAt().toLocalDateTime().toLocalDate();
      dailyRequestCounts.put(date, dailyRequestCounts.getOrDefault(date, 0) + 1);
    }

    // Populate BarChart
    XYChart.Series<String, Number> barChartData = new XYChart.Series<>();
    for (Map.Entry<RequestType, Integer> entry : requestTypeCounts.entrySet()) {
      barChartData.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
    }
    barChart.getData().add(barChartData);

    // Populate PieChart
    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    for (Map.Entry<Status, Integer> entry : statusCounts.entrySet()) {
      pieChartData.add(new PieChart.Data(entry.getKey().toString(), entry.getValue()));
    }
    pieChart.setData(pieChartData);

    // Populate barChart2
    XYChart.Series<String, Number> barChart2Data = new XYChart.Series<>();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    for (Map.Entry<LocalDate, Integer> entry : dailyRequestCounts.entrySet()) {
      barChart2Data
          .getData()
          .add(new XYChart.Data<>(entry.getKey().format(dateFormatter), entry.getValue()));
    }

    barChart2.getData().add(barChart2Data);
  }
}

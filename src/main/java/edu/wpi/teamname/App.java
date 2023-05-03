package edu.wpi.teamname;

import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.extras.Sound;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App extends Application {

  @Setter @Getter private static Stage primaryStage;
  @Setter @Getter private static BorderPane rootPane;
  private boolean loading;

  private Timeline idleTimeline;

  @Override
  public void init() {
    log.info("Starting Up");
  }

  @Override
  public void start(Stage primaryStage) throws IOException, SQLException {
    //    LoadingScreen l = new LoadingScreen();
    //    l.displayLoading();

    /* primaryStage is generally only used if one of your components require the stage to display */
    App.primaryStage = primaryStage;
    primaryStage.setFullScreen(true);

    DataManager.connectToWPI();

    final FXMLLoader loader = new FXMLLoader(App.class.getResource("views/Root.fxml"));
    final BorderPane root = loader.load();

    App.rootPane = root;

    final Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.show();

    Navigation.navigate(Screen.HOME);

    // SCREENSAVER PLAYTIME

    //    Timeline timeline =
    //        new Timeline(
    //            new KeyFrame(
    //                Duration.seconds(5),
    //                event -> {
    //                  // Code to be executed after 20 seconds
    //                  ss.startScreenSaver(root);
    //                }));
    //    timeline.play();

    // Create a text node to display the idle time

    // Create an event handler for mouse movement
    root.setOnMouseMoved(
            event -> {
              // Reset the idle timeline when the mouse is moved
              resetIdleTimeline(root);
            });

    root.setOnKeyPressed(
            event -> {
              resetIdleTimeline(root);
            });

    // Create a timeline to track idle time
    idleTimeline =
            new Timeline(
                    new KeyFrame(
                            Duration.seconds(1),
                            event -> {
                              count.getAndIncrement();
                              // Update the idle time display
                              System.out.println("Time: " + count);

                              // Do something when the idle time exceeds a certain duration
                              if (
                                /*idleTimeline.getCurrentTime().greaterThanOrEqualTo(Duration.seconds(5)*/ count
                                      .get()
                                      == 5
                                      && !flag) {
                                flag = true;
                                GlobalVariables.getSs().startScreenSaver(root);
                              }
                            }));
    idleTimeline.setCycleCount(Timeline.INDEFINITE);
    idleTimeline.play();
  }

  private void resetIdleTimeline(BorderPane root) {
    // Stop and restart the idle timeline to reset the idle time
    System.out.println("This ran");
    count.getAndSet(0);
    flag = false;
    GlobalVariables.getSs().stopScreenSaver(root);
    idleTimeline.stop();
    idleTimeline.playFromStart();
  }

  @Override
  public void stop() throws SQLException {
    Connection connection = DataManager.DbConnection();
    connection.close();
    log.info("Shutting Down");
  }

  //  public void screenSaver(){//every 5 minutes
  //    Timer timer;
  //    timer.schedule(showScreenSaver(), 300000);
  //  }
}

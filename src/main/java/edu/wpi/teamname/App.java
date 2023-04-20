package edu.wpi.teamname;

import edu.wpi.teamname.database.DataManager;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App extends Application {

  @Setter @Getter private static Stage primaryStage;
  @Setter @Getter private static BorderPane rootPane;
  private boolean loading;

  @Override
  public void init() {
    log.info("Starting Up");
  }

  @Override
  public void start(Stage primaryStage) throws IOException, InterruptedException {

    Navigation.navigate(Screen.LOADING);
    Thread.sleep(5 * 1000);
    //    LoadingScreenController l = new LoadingScreenController();
    //    l.initialize(3);

    /* primaryStage is generally only used if one of your components require the stage to display */
    App.primaryStage = primaryStage;
    primaryStage.setFullScreen(true);

    final FXMLLoader loader = new FXMLLoader(App.class.getResource("views/Root.fxml"));
    final BorderPane root = loader.load();

    App.rootPane = root;
    final Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.show();

    Navigation.navigate(Screen.HOME);
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

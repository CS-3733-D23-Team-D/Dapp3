package edu.wpi.teamname;

import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.extras.Sound;
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
  public void start(Stage primaryStage) throws IOException, SQLException {
    //    LoadingScreen l = new LoadingScreen();
    //    l.displayLoading();

    /* primaryStage is generally only used if one of your components require the stage to display */

    Sound.playMusic();
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

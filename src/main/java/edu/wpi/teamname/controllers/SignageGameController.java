package edu.wpi.teamname.controllers;

import java.util.ArrayList;

import edu.wpi.teamname.navigation.Signage;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class SignageGameController {
    @FXML VBox contentVBox;
    @FXML VBox arrowVbox;

    public void initialize() {
        ArrayList<Signage> signs = new ArrayList<>();
        //get current signage
        //set signs to each signage
        //loop thru and display all them

        for (int i = 0; i < signs.size(); i++) {
            // contentVBox.getChildren().add();
            // arrowVbox.getChildren().add()
        }
    }
}
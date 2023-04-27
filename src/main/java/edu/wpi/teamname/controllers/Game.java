package edu.wpi.teamname.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Game {
    @FXML MFXButton play;
    @FXML Label textSpace;
    @FXML Label enterSpace;
    @FXML Label score;

    MFXButton scene = new MFXButton();

    private static Random rand = new Random();
    private static int scoreCount = 0;
    private static long timeBetween = 5;
    private static ArrayList<String> choices = new ArrayList<>();
    private static String chosen = "";
    private static boolean on = false;
    private static boolean entered = false;
    private static long timer = 0;


    public void initialize() throws InterruptedException {
        enterSpace.setVisible(false);
        textSpace.setVisible(false);
        score.setVisible(false);
        //set cursor to be on textLabel
        choices.add("W");
        choices.add("A");
        choices.add("S");
        choices.add("D");
        choices.add("SPACE");
        choices.add("I");
        choices.add("J");
        choices.add("K");
        choices.add("L");


        //on play
        play.setOnAction(event -> {
            on = true;
                play.setVisible(false);
                play.setDisable(true);
                score.setVisible(true);
                enterSpace.setVisible(true);
                textSpace.setVisible(true);
                try {
                    while (on) {
                        TimeUnit.SECONDS.sleep(timeBetween);
                        spawn();
                    }
                } catch (InterruptedException e) {
                    System.out.println(e);
                }

        });
    }

    private void spawn() throws InterruptedException {
        chooseText();
        while(!entered) {
            timer++;
            scene.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    entered = true;
                    timer = 0;
                    String value = textSpace.getText();
                    System.out.println(value + " Entered");
                    if(value.equals(chosen)){
                        scoreCount += 1;
                        score.setText("Score: " + scoreCount);
                    }
                    else {
                        on = false;
                        textSpace.setText("Wrong!");
                        textSpace.setVisible(false);
                        play.setVisible(true);
                    }
                }
            });
        }
    }

    private static void chooseText(){
        int random = rand.nextInt(0, choices.size());
        for(int i=0;i<choices.size();i++){
            chosen = choices.get(i);
        }
    }

}

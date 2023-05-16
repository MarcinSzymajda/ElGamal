package com.example.kryptozad2;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        MainController mainController = new MainController();
        mainController.showStage();
    }

    public static void main(String[] args) {
        launch();
    }
}
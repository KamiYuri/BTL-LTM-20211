package com.ltm.client;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage){
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("view/login-view.fxml")));
            Scene scene = new Scene(root);

            stage.setTitle("Hello!");
            stage.setScene(scene);
            stage.setFullScreen(false);
            stage.setResizable(false);

            stage.show();
        }catch (IOException e){
            System.out.println(e);
        }

    }

    public static void main(String[] args) {
        launch();
    }
}
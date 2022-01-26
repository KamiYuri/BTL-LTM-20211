package com.ltm.client;

import com.ltm.client.Utils.SocketCommunicator;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class Main extends Application{
    @Override
    public void start(Stage stage){
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("view/login-view.fxml")));
            Scene scene = new Scene(root);

            stage.setTitle("Hello!");
            stage.setScene(scene);
            stage.setFullScreen(false);
            stage.setResizable(false);

            stage.setOnCloseRequest(windowEvent -> {
                windowEvent.consume();
                try {
                    close(stage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            stage.show();

            try {
                SocketCommunicator.getInstance();
            } catch (IOException e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Connection errror");
                alert.setHeaderText("Cannot connect to server");
                alert.setContentText(e.getMessage());
                if(alert.showAndWait().get() == ButtonType.OK){
                    stage.close();
                }
            }

        }catch (IOException e){
            System.out.println(e);
        }

    }

    private void close(Stage stage) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Close");
        alert.setHeaderText("Disconnect to server?");
        if(alert.showAndWait().get() == ButtonType.OK){
            SocketCommunicator.getInstance().getConnSocket().close();
            stage.close();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
package com.kamiyuri;

import com.kamiyuri.view.ViewFactory;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {
    private AuctionManager auctionManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            this.auctionManager = new AuctionManager();
        } catch (IOException e){
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("loi");
//            alert.setHeaderText("loi ket noi");
//            alert.setContentText(e.getMessage());
//
//            alert.showAndWait();
//            return;
        }

        ViewFactory viewFactory = new ViewFactory(auctionManager);
        viewFactory.showLoginWindow();
    }
}

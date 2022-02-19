package com.kamiyuri.view;

import com.kamiyuri.AuctionManager;
import com.kamiyuri.controller.BaseController;
import com.kamiyuri.controller.CreateRoomWindowController;
import com.kamiyuri.controller.LoginWindowController;
import com.kamiyuri.controller.MainWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;

public class ViewFactory {
    private final AuctionManager auctionManager;
    private final ArrayList<Stage> activeStages;

    public ViewFactory(AuctionManager auctionManager) {
        this.auctionManager = auctionManager;
        this.activeStages = new ArrayList<Stage>();
    }

    private void initializeStage(BaseController baseController) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(baseController.getFxmlName()));
        fxmlLoader.setController(baseController);
        Parent parent;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Scene scene = new Scene(parent);
        Stage stage = new Stage();


        stage.setScene(scene);
        stage.setResizable(false);

//        stage.setOnCloseRequest(event -> {
//            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//            Optional<ButtonType> option = alert.showAndWait();
//            if(option.get() == ButtonType.OK){
//                auctionManager.diconnect();
//                stage.close();
//            } else if(option.get() == ButtonType.CANCEL) {
//                return;
//            }
//
//        });
        stage.show();
        activeStages.add(stage);
    }

    public void closeStage(Stage stageToClose) {
        stageToClose.close();
        activeStages.remove(stageToClose);
    }


    public void showLoginWindow() {
        BaseController controller = new LoginWindowController(auctionManager, this, "LoginWindow.fxml");
        initializeStage(controller);
    }

    public void showMainWindow() {
        BaseController controller = new MainWindowController(auctionManager, this, "MainWindow.fxml");
        initializeStage(controller);
    }
}

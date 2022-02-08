package com.kamiyuri.view;

import com.kamiyuri.AuctionManager;
import com.kamiyuri.controller.BaseController;
import com.kamiyuri.controller.LoginWindowController;
import com.kamiyuri.controller.MainWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class ViewFactory {
    private AuctionManager auctionManager;
    private ArrayList<Stage> activeStages;

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
        stage.show();
        activeStages.add(stage);
    }

    public void closeStage(Stage stageToClose){
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

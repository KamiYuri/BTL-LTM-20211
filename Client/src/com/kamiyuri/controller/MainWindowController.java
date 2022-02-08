package com.kamiyuri.controller;

import com.kamiyuri.AuctionManager;
import com.kamiyuri.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController extends BaseController implements Initializable {

    @FXML
    private TreeView<?> roomTreeView;

    @FXML
    void createRoomAction() {

    }

    @FXML
    void logoutAction() {

    }

    public MainWindowController(AuctionManager auctionManager, ViewFactory viewFactory, String fxmlName) {
        super(auctionManager, viewFactory, fxmlName);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpRoomTreeView();
    }

    private void setUpRoomTreeView() {

    }
}

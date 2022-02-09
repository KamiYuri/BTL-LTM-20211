package com.kamiyuri.controller;

import com.kamiyuri.AuctionManager;
import com.kamiyuri.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController extends BaseController implements Initializable {

    @FXML
    private TreeView<String> roomTreeView;

    @FXML
    private TreeView<String> userRoomTreeView;

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
        setUpTreeView();
    }

    private void setUpTreeView() {
        TreeItem<String> room = new TreeItem<>("Room");
        room.getChildren().add(new TreeItem<>("a"));
        room.setExpanded(true);
        roomTreeView.setRoot(room);

        TreeItem<String> userRoom = new TreeItem<>("My room");
        userRoom.getChildren().add(new TreeItem<>("a"));
        userRoom.setExpanded(true);
        userRoomTreeView.setRoot(userRoom);
    }
}

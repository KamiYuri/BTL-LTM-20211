package com.kamiyuri.controller;

import com.kamiyuri.AuctionManager;
import com.kamiyuri.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController extends BaseController implements Initializable {

    @FXML
    private TreeView<String> roomTreeView;

    @FXML
    private TreeView<String> userRoomTreeView;

    @FXML
    private Label userId;

    @FXML
    private Label userName;

    @FXML
    private Button refreshBtn;

    @FXML
    void createRoomAction() {

    }

    @FXML
    void logoutAction() {

    }

    @FXML
    void refreshRoomsAction() {

    }

    public MainWindowController(AuctionManager auctionManager, ViewFactory viewFactory, String fxmlName) {
        super(auctionManager, viewFactory, fxmlName);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpUserInf();
        setUpTreeView();
        setUpRefreshBtn();
    }

    private void setUpUserInf() {
        this.userId.setText(auctionManager.getUserId());
        this.userName.setText(auctionManager.getUserName());
    }

    private void setUpRefreshBtn() {
//        Image image = new Image("");
//        ImageView imageView = new ImageView(image);
//        refreshBtn.setGraphic(imageView);
    }

    private void setUpTreeView() {
        TreeItem<String> room = new TreeItem<>("Room");
        room.setExpanded(true);
        room.getChildren().add(new TreeItem<>("a"));
        room.getChildren().add(new TreeItem<>("a"));
        room.getChildren().add(new TreeItem<>("a"));
//        auctionManager.getRooms(room);
        roomTreeView.setRoot(room);

        TreeItem<String> userRoom = new TreeItem<>("My room");
        userRoom.setExpanded(true);
//        auctionManager.getUserRooms(userRoom);
        userRoomTreeView.setRoot(userRoom);
    }
}

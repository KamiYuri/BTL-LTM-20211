package com.kamiyuri.controller;

import com.kamiyuri.AuctionManager;
import com.kamiyuri.TCP.RequestType;
import com.kamiyuri.model.Room;
import com.kamiyuri.model.RoomTreeItem;
import com.kamiyuri.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController extends BaseController implements Initializable {

    private Room selectedRoom;

    @FXML
    private Label itemBuyPriceLabel;

    @FXML
    private Label itemCurrentPriceLabel;

    @FXML
    private Label itemDescriptionLabel;

    @FXML
    private Label itemNameLabel;

    @FXML
    private Button refreshBtnAction;

    @FXML
    private Label roomIdLabel;

    @FXML
    private AnchorPane roomPane;

    @FXML
    private Label userIdLabel;

    @FXML
    private Label userNameLabel;
    @FXML
    private TreeView<String> roomTreeView;

    public MainWindowController(AuctionManager auctionManager, ViewFactory viewFactory, String fxmlName) {
        super(auctionManager, viewFactory, fxmlName);
    }

    @FXML
    void bidBtnAction() {
        showPopup(RequestType.BID);
    }

    @FXML
    void buyBtnAction() {
        showPopup(RequestType.BUY);
    }

    @FXML
    void createRoomAction() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CreateRoomWindow.fxml"));
        fxmlLoader.setController(new CreateRoomWindowController(auctionManager));
        Parent parent;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Scene scene = new Scene(parent);
        Stage stage = new Stage();

        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(roomIdLabel.getScene().getWindow().getScene().getWindow());

        stage.setScene(scene);
        stage.setResizable(false);

        stage.show();
    }

    @FXML
    void logoutAction() {

    }

    @FXML
    void refreshRoomsAction() {
        auctionManager.getRooms(roomTreeView.getRoot());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpRoomPane();
        setUpUserInf();
        setUpTreeView();
    }

    private void setUpRoomPane() {
        roomPane.getChildren().forEach(node -> {
            node.setVisible(false);
        });
    }

    private void setUpUserInf() {
        this.userIdLabel.setText("Mã tài khoản: " + auctionManager.getUserId());
        this.userNameLabel.setText("Tài khoản: " + auctionManager.getUserName());
    }

    private void setUpTreeView() {
        TreeItem<String> room = new TreeItem<>("");
        room.setExpanded(true);
        auctionManager.getRooms(room);
        roomTreeView.setRoot(room);
        roomTreeView.setShowRoot(false);

        roomTreeView.setOnMouseClicked(event -> {
            RoomTreeItem<String> item = (RoomTreeItem<String>) roomTreeView.getSelectionModel().getSelectedItem();
            if (item != null) {
                Room selectedRoom = auctionManager.getSelectedRoom(item.getValue());

                roomIdLabel.setText("Phòng số " + selectedRoom.getRoomId());
                itemNameLabel.setText("Tên vật phẩm: " + selectedRoom.getItemName());
                itemDescriptionLabel.setText("Mô tả: " + selectedRoom.getItemDescription());
                itemCurrentPriceLabel.setText("Giá hiện tại: " + selectedRoom.getCurrentPrice() + " VNĐ");
                itemBuyPriceLabel.setText("Giá mua: " + selectedRoom.getBuyImmediatelyPrice() + " VNĐ");

                roomPane.getChildren().forEach(node -> {
                    node.setVisible(true);
                });
            }
        });

    }

    private void showPopup(RequestType type) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("popup.fxml"));
        fxmlLoader.setController(new Popup(type, auctionManager));
        Parent parent;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Scene scene = new Scene(parent);
        Stage stage = new Stage();

        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(roomIdLabel.getScene().getWindow().getScene().getWindow());

        stage.setScene(scene);
        stage.setResizable(false);

        stage.showAndWait();
    }
}

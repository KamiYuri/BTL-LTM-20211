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
import java.util.function.Consumer;

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

    @FXML
    private Button bidBtn;

    @FXML
    private Button buyBtn;

    @FXML
    private Label noticLabel;

    @FXML
    private Label soldLabel;

    public MainWindowController(AuctionManager auctionManager, ViewFactory viewFactory, String fxmlName) {
        super(auctionManager, viewFactory, fxmlName);
    }

    @FXML
    void bidBtnAction() {
        Popup popupController = new Popup(RequestType.BID, auctionManager);
        popupController.setCallback(price -> auctionManager.bid(price));
        showPopup(RequestType.BID, popupController);
    }

    @FXML
    void buyBtnAction() {
        Popup popupController = new Popup(RequestType.BUY, auctionManager);
        popupController.setCallback(unused -> auctionManager.buy());
        showPopup(RequestType.BUY, popupController);
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
        auctionManager.setLockBidCallback(lockBid);
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

        auctionManager.setRoot(roomTreeView.getRoot());


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
                    if(node != bidBtn & node != buyBtn & node != noticLabel & node != soldLabel) {
                        node.setVisible(true);
                    }
                });
            }
        });

    }

    private void showPopup(RequestType type, Popup popupController) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("popup.fxml"));
        fxmlLoader.setController(popupController);
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

        if(type == RequestType.BID){
            popupController.set();
        }

        stage.showAndWait();
    }

    private Consumer<String> lockBid = str -> {
        if(str.equals("SUCCESS")){
                bidBtn.setVisible(true);
                buyBtn.setVisible(true);
                soldLabel.setVisible(false);
                noticLabel.setVisible(false);
        } else {
            bidBtn.setVisible(false);
            buyBtn.setVisible(false);
            soldLabel.setVisible(true);
            noticLabel.setVisible(true);

            switch (str){
                case "NO_BUY":
                    soldLabel.setText("Đã kết thúc đấu giá. Không ai mua được vật phẩm.");
                    break;
                case "BOUGHT":
                    soldLabel.setText("Đã kết thúc đấu giá và bạn đã mua được vật phẩm.");
                    break;
                case "SOLD":
                    soldLabel.setText("Đã kết thúc đấu giá và có người đã mua được vật phẩm.");
                    break;
            }
        }



    };

}

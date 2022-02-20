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
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
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

    private class BidPopup{

        @FXML
        private TextField bidPriceField;

        Boolean result = false;
        String input;

        @FXML
        void cancelBtnAction() {
            close();
        }

        @FXML
        void submitBtnAction() {
            result = true;
            input = bidPriceField.getText();
            close();
        }

        void close(){
            Stage stage = (Stage) bidPriceField.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    void bidBtnAction() {
        BidPopup bidPopup = new BidPopup();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("bidPopup.fxml"));
        fxmlLoader.setController(bidPopup);
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

        if(bidPopup.result){
            auctionManager.bid(bidPopup.input);
        }
    }

    @FXML
    void buyBtnAction() {
        Consumer<Void> callback = unused -> {
            auctionManager.buy();
        };
        Popup popup = new Popup((Stage) itemNameLabel.getScene().getWindow());
        popup.show(callback,"Bạn chắc chắn muốn mua luôn vật phẩm này?");
    }
    private Popup popup;

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

        popup = new Popup((Stage) itemNameLabel.getScene().getWindow());
    }

    @FXML
    void logoutAction() {
        Consumer<Void> callback = unused -> {
            auctionManager.logout();
        };
        Popup popup = new Popup((Stage) itemNameLabel.getScene().getWindow());
        popup.show(callback, "Bạn chắc chắn muốn đăng xuất?");

        if(auctionManager.handleLogout()){
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.close();
            viewFactory.showLoginWindow();
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Xảy ra lỗi khi đăng xuất.");
            alert.showAndWait();
        }
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
                auctionManager.getRooms(roomTreeView.getRoot());
                Room selectedRoom = auctionManager.getSelectedRoom(item.getValue());

                roomIdLabel.setText("Phòng số " + selectedRoom.getRoomId());
                itemNameLabel.setText("Tên vật phẩm: " + selectedRoom.getItemName());
                itemDescriptionLabel.setText("Mô tả: " + selectedRoom.getItemDescription());
                itemCurrentPriceLabel.setText("Giá hiện tại: " + selectedRoom.getCurrentPrice() + " VNĐ");
                itemBuyPriceLabel.setText("Giá mua: " + selectedRoom.getBuyImmediatelyPrice() + " VNĐ");

                roomPane.getChildren().forEach(node -> {
                    if (node != bidBtn & node != buyBtn & node != noticLabel & node != soldLabel) {
                        node.setVisible(true);
                    }
                });
            }
        });

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
        }
    };
}

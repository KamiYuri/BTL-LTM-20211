package com.kamiyuri.controller;

import com.kamiyuri.AuctionManager;
import com.kamiyuri.model.Room;
import com.kamiyuri.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

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
    void bidBtnAction() {

    }

    @FXML
    void buyBtnAction() {

    }

    @FXML
    void createRoomAction() {

    }

    @FXML
    void logoutAction() {

    }

    @FXML
    void refreshRoomsAction() {

    }

    @FXML
    private TreeView<String> roomTreeView;

    public MainWindowController(AuctionManager auctionManager, ViewFactory viewFactory, String fxmlName) {
        super(auctionManager, viewFactory, fxmlName);
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
        TreeItem<String> room = new TreeItem<>("Room");
        room.setExpanded(true);
        room.getChildren().add(new TreeItem<>("a"));
        room.getChildren().add(new TreeItem<>("b"));
        room.getChildren().add(new TreeItem<>("c"));
//        auctionManager.getRooms(room);
        roomTreeView.setRoot(room);
        roomTreeView.setShowRoot(false);

        roomTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Node node = event.getPickResult().getIntersectedNode();
            if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
                TreeItem selectedItem = roomTreeView.getSelectionModel().getSelectedItem();
                showRoom(selectedItem);
            }
        });
    }

    private void showRoom(TreeItem selectedItem) {

        Room selectedRoom = getSelectedRoom((String) selectedItem.getValue());

        roomIdLabel.setText("Phòng số " + selectedRoom.getRoomId());
        itemNameLabel.setText("Tên vật phẩm: " + selectedRoom.getItemName());
        itemDescriptionLabel.setText("Mô tả: " + selectedRoom.getItemDescription());
        itemCurrentPriceLabel.setText("Giá hiện tại: " + selectedRoom.getCurrentPrice() + " VNĐ");
        itemBuyPriceLabel.setText("Giá mua: " + selectedRoom.getBuyImmediatelyPrice() + " VNĐ");

        roomPane.getChildren().forEach(node -> {
            node.setVisible(true);
        });
    }

    private Room getSelectedRoom(String value) {
       Room room = new Room(
                value,
                "abc",
                "asdsd sadasd asdsad",
                "123",
                "12314"
        );

       return room;
    }
}

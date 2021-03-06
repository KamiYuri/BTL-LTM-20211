package com.kamiyuri.controller;

import com.kamiyuri.AuctionManager;
import com.kamiyuri.controller.services.*;
import com.kamiyuri.model.Room;
import com.kamiyuri.view.ViewFactory;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Scanner;

public class MainWindowController extends BaseController implements Initializable {
    private Room selectedRoom;
    private ObservableList<String> notifications;

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
    private Label notificationLabel;

    public MainWindowController(AuctionManager auctionManager, ViewFactory viewFactory, String fxmlName) {
        super(auctionManager, viewFactory, fxmlName);
    }

    @FXML
    void createRoomAction() {
        viewFactory.showCreateRoomWindow();
    }

    @FXML
    void logoutAction() {
        LogoutService logoutService = new LogoutService(auctionManager);
        logoutService.start();
        logoutService.setOnSucceeded(event -> {
            LogoutResult logoutResult = logoutService.getValue();

            if (logoutResult == LogoutResult.SUCCESS) {
                viewFactory.closeStage((Stage) itemNameLabel.getScene().getWindow());
                viewFactory.showLoginWindow();
            }
        });
    }

    @FXML
    void refreshRoomsAction() {
        auctionManager.setGetRoomsResponse(null);
        setUpTreeView();
    }

    @FXML
    void bidBtnAction() {
        viewFactory.showBidWindow();
    }

    @FXML
    void buyBtnAction() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("B???n ch???c ch???n mu???n mua v???t ph???m n??y?");
        Optional<ButtonType> result = alert.showAndWait();


        if (result.get() == ButtonType.OK) {
            BuyService buyService = new BuyService(auctionManager);
            buyService.start();
            buyService.setOnSucceeded(event -> {
                BuyResult buyResult = buyService.getValue();

                switch (buyResult) {
                    case SUCCESS: {
                        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                        alert1.setHeaderText("Mua th??nh c??ng.");
                        alert1.setContentText("B???n ???? mua th??nh c??ng v???t ph???m.");
                        alert1.showAndWait();
                        break;
                    }
                    case ALREADY_SOLD: {
                        Alert alert1 = new Alert(Alert.AlertType.ERROR);
                        alert1.setHeaderText("Mua v???t ph???m th???t b???i");
                        alert1.setContentText("V???t ph???m ???? ???????c mua b???i ng?????i kh??c.");
                        alert1.showAndWait();
                        showData(false);
                        notificationLabel.setText("V???t ph???m ???? ???????c mua b???i ng?????i kh??c.");
                        break;
                    }
                    case CREATOR_CANT_BID: {
                        Alert alert1 = new Alert(Alert.AlertType.ERROR);
                        alert1.setHeaderText("Mua v???t ph???m th???t b???i");
                        alert1.setContentText("Ch??? ph??ng kh??ng th??? mua v???t ph???m.");
                        alert1.showAndWait();
                        break;
                    }
                }
            });
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpRoomPane();
        setUpUserInf();
        setUpTreeView();
        setUpRoomSelection();
        setUpNotificationHandle();
//        setUpCloseConnection();
    }

    private void setUpCloseConnection() {
        Stage stage = (Stage) itemNameLabel.getScene().getWindow();
        stage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("T???t ch????ng tr??nh?");

            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                auctionManager.diconnect();
                stage.close();
            }
        });
    }

    private void setUpNotificationHandle() {
        this.notifications = auctionManager.getNotifications();
        notifications.addListener((ListChangeListener<? super String>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    handleNotification(change.getList(), this.notificationLabel);
                }
            }
        });
    }

    private void handleNotification(ObservableList<? extends String> notificationList, Label notificationLabel) {
        String notification = notificationList.get(notificationList.size() - 1);

        switch (notification.substring(0, 2)) {
            case "80":
                Platform.runLater(() -> {
                    if (notification.substring(2) == auctionManager.getAccount().getUserId()) {
                        notificationLabel.setText("B???n ???? mua th??nh c??ng v???t ph???m.");
                    } else {
                        notificationLabel.setText("V???t ph???m ???? ???????c mua b???i ng?????i kh??c.");
                    }
                    notificationLabel.setVisible(true);
                });
                break;
            case "81":
                Platform.runLater(() -> {
                    notificationLabel.setText("Th???i gian c??n l???i " + notification.substring(2) + " ph??t.");
                    if (notification.substring(2) == "80") {
                        bidBtn.setVisible(false);
                        buyBtn.setVisible(false);
                    }
                    notificationLabel.setVisible(true);
                });
                break;
            case "82":
                Platform.runLater(() -> {
                    itemCurrentPriceLabel.setText("Gi?? hi???n t???i: " + notification.substring(2) + " VN??");
                });
                break;
        }

    }

    private void setUpRoomPane() {
        roomPane.getChildren().forEach(node -> {
            node.setVisible(false);
        });
    }

    private void setUpUserInf() {
        this.userIdLabel.setText("M?? t??i kho???n: " + auctionManager.getAccount().getUserId());
        this.userNameLabel.setText("T??i kho???n: " + auctionManager.getAccount().getUsername());
    }

    private void setUpTreeView() {
        roomTreeView.setRoot(auctionManager.getTreeRoot());
        roomTreeView.setShowRoot(false);
    }

    private void setUpRoomSelection() {
        roomTreeView.setOnMouseClicked(e -> {
            TreeItem<String> item = roomTreeView.getSelectionModel().getSelectedItem();
            if (item != null) {
                FilteredList<Room> roomFilteredList = auctionManager.getRoomList().filtered(room -> room.getRoomId().equals(item.getValue()));
                selectedRoom = roomFilteredList.get(0);

                auctionManager.setSelectedRoom(selectedRoom);
                System.out.println(auctionManager.getSelectedRoom().getRoomId());

                auctionManager.setJoinRoomResponse(null);

                Properties data = new Properties();
                data.put("userId", auctionManager.getAccount().getUserId());
                data.put("roomId", auctionManager.getSelectedRoom().getRoomId());

                auctionManager.setJoinRoomResponse(null);
                String request = RequestFactory.getRequest(RequestType.JOIN_ROOM, data);
                System.out.println("join request" + request);
                auctionManager.sendRequest(request);
                String response;
                do {
                    response = auctionManager.getJoinRoomResponse();
                } while (response == null);

                JoinRoomResult joinRoomResult;

                if (response.charAt(1) == '1') {
                    Scanner scanner = new Scanner(response.substring(2));
                    scanner.useDelimiter(Delimiter.Two());

                    String owner = scanner.next();
                    String currentPrice = scanner.next();

                    auctionManager.setSelectedRoomCurrentPrice(currentPrice);

                    if (owner.equals("0")) {
                        joinRoomResult = JoinRoomResult.NO_ONE_BUY;
                    } else {
                        if (owner.equals(auctionManager.getAccount().getUserId())) {
                            joinRoomResult = JoinRoomResult.BOUGHT_BY_USER;
                        } else {
                            joinRoomResult = JoinRoomResult.BOUGHT_BY_ANOTHER;
                        }
                    }
                } else {
                    joinRoomResult = JoinRoomResult.SUCCESS;
                }

                switch (joinRoomResult) {
                    case SUCCESS:
                        showData(true);
                        break;
                    case BOUGHT_BY_ANOTHER:
                        notificationLabel.setText("V???t ph???m ???? ???????c mua b???i ng?????i kh??c.");
                        showData(false);
                        break;
                    case NO_ONE_BUY:
                        notificationLabel.setText("Phi??n ?????u gi?? ???? k???t th??c. V???t ph???m ch??a ???????c b??n.");
                        showData(false);
                        break;
                    case BOUGHT_BY_USER:
                        notificationLabel.setText("Ch??c m???ng. B???n ???? mua ???????c v???t ph???m");
                        showData(false);
                        break;
                }
            }
        });
    }

    private void showData(boolean joinSuccess) {
        Room selectedRoom = auctionManager.getSelectedRoom();

        roomIdLabel.setText("Ph??ng s??? " + selectedRoom.getRoomId());
        itemNameLabel.setText("T??n v???t ph???m: " + selectedRoom.getItemName());
        itemDescriptionLabel.setText("M?? t???: " + selectedRoom.getItemDescription());
        itemCurrentPriceLabel.setText("Gi?? hi???n t???i: " + selectedRoom.getCurrentPrice() + " VN??");
        itemBuyPriceLabel.setText("Gi?? mua: " + selectedRoom.getBuyImmediatelyPrice() + " VN??");
        roomPane.getChildren().forEach(node -> {
            if (node != bidBtn & node != buyBtn & node != notificationLabel) {
                node.setVisible(true);
            }
        });

        notificationLabel.setVisible(!joinSuccess);
        bidBtn.setVisible(joinSuccess);
        buyBtn.setVisible(joinSuccess);
    }
}

package com.kamiyuri;

import com.kamiyuri.TCP.ConnectionThread;
import com.kamiyuri.TCP.Delimiter;
import com.kamiyuri.TCP.RequestFactory;
import com.kamiyuri.TCP.RequestType;
import com.kamiyuri.model.Account;
import com.kamiyuri.model.Room;
import com.kamiyuri.model.RoomTreeItem;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class AuctionManager {
    private final ConnectionThread connectionThread;
    private TreeItem<String> root;
    private ObservableList<Room> roomObservableList = FXCollections.observableArrayList();
    private Room selectedRoom;
    private Account account;
    private String loginResponse, logoutResponse, roomResponse, createRoomResponse, joinRoomResponse, bidResponse, buyResponse;
    private ObservableList<String> noticfications = FXCollections.observableArrayList();

    private Consumer<String> lockBidCallback, noticCallback;

    private Consumer<String> getResponseCallback = response -> {
        RequestType code = RequestType.values()[Character.getNumericValue(response.charAt(0)) - 1];

        switch (code) {
            case LOGIN:
                loginResponse = response;
                break;
            case LOGOUT:
                break;
            case SHOW_ROOM:
                roomResponse = response;
                break;
            case CREATE_ROOM:
                createRoomResponse = response;
                break;
            case JOIN_ROOM:
                joinRoomResponse = response;
                break;
            case BID:
                bidResponse = response;
                break;
            case BUY:
                buyResponse = response;
                break;
            case NOTIFICATION:
                noticfications.add(response);
                break;
        }
    };

    public AuctionManager() throws IOException {
        this.connectionThread = new ConnectionThread();
        setupConnectionThread();
        this.connectionThread.start();

        noticfications.addListener((ListChangeListener<? super String>) observable -> {
            System.out.println(noticfications.get(0));
            noticfications.remove(0);
        });
    }

    private void setupConnectionThread() {
        this.connectionThread.setResponseCallback(this.getResponseCallback);
    }

    public TreeItem<String> getRoomsRoot() {
        return this.root;
    }

    public void setRoot(TreeItem<String> root) {
        this.root = root;
    }

    public void getRooms(TreeItem<String> roomTreeItem) {
        roomResponse = null;
        Service service = new Service() {
            @Override
            protected Task createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        String request = RequestFactory.getRequest(RequestType.SHOW_ROOM, null);
                        sendRequest(request);
                        String t;
                        do {
                            t = roomResponse;
                        } while (t == null);
                        return null;
                    }
                };
            }
        };

        service.start();
        service.setOnSucceeded(event -> {
            handleRoomsRespone();
            roomTreeItem.getChildren().clear();

            for (Room room : roomObservableList) {
                RoomTreeItem treeItem = new RoomTreeItem<String>(room.getRoomId());
                roomTreeItem.getChildren().add(treeItem);
            }
        });
    }

    private void handleRoomsRespone() {

        roomObservableList.clear();
        Scanner roomsScanner = new Scanner(roomResponse.substring(2));
        roomsScanner.useDelimiter(Delimiter.Two());
        while(roomsScanner.hasNext()){
            Scanner roomDataScanner = new Scanner(roomsScanner.next());
            roomDataScanner.useDelimiter(Delimiter.Three());

            String roomId = roomDataScanner.next();
            String itemName = roomDataScanner.next();
            String itemDescription = roomDataScanner.next();
            String itemCurrentPrice = roomDataScanner.next();
            String itemBuyImmediatelyPrice = roomDataScanner.next();

            Room room = new Room(
                    roomId,
                    itemName,
                    itemDescription,
                    itemCurrentPrice,
                    itemBuyImmediatelyPrice
            );

            roomObservableList.add(room);
        }
    }

    public Room getSelectedRoom(String roomId){
        FilteredList<Room> roomFilteredList = roomObservableList.filtered(room -> room.getRoomId().equals(roomId));
        if (selectedRoom != null && selectedRoom != roomFilteredList.get(0)) {
            leaveRoom(selectedRoom);
        }
        selectedRoom = roomFilteredList.get(0);
        joinRoom(selectedRoom);

        return selectedRoom;
    }

    private void joinRoom(Room selectedRoom) {
        Service service = new Service() {
            @Override
            protected Task createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        Properties data = new Properties();
                        data.put("userId", account.getUserId());
                        data.put("roomId", selectedRoom.getRoomId());

                        String request = RequestFactory.getRequest(RequestType.JOIN_ROOM, data);
                        sendRequest(request);
                        return null;
                    }
                };
            }
        };

        service.start();

        service.setOnSucceeded(event -> {
            if(joinRoomResponse.charAt(1) == '1'){
                Scanner scanner = new Scanner(joinRoomResponse.substring(2));
                scanner.useDelimiter(Delimiter.Two());

                String owner = scanner.next();
                String currentPrice = scanner.next();

                if(owner.equals('0')){
                    lockBidCallback.accept("NO_BUY");
                }

                else {
                    if(owner.equals(account.getUserId())) {
                        lockBidCallback.accept("BOUGHT");
                    }
                    else {
                        lockBidCallback.accept("SOLD");
                    }
                }
            } else {
                lockBidCallback.accept("SUCCESS");
            }
        });
    }

    private void leaveRoom(Room selectedRoom) {
        Service service = new Service() {
            @Override
            protected Task createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        Properties data = new Properties();
                        data.put("userId", account.getUserId());
                        data.put("roomId", selectedRoom.getRoomId());

                        String request = RequestFactory.getRequest(RequestType.LEAVE_ROOM, data);
                        sendRequest(request);
                        return null;
                    }
                };
            }
        };
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getUserId() {
        return account.getUserId();
    }

    public String getUserName() {
        return account.getUsername();
    }

    public void sendRequest(String request) {
        this.connectionThread.send(request);
    }

    public String getLoginResponse() {
        return loginResponse;
    }

    public String getLogoutResponse() {
        return logoutResponse;
    }

    public void createRoom(Properties data) {
        createRoomResponse = null;
        data.put("userId", account.getUserId());

        Service service = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws Exception {
                        String request = RequestFactory.getRequest(RequestType.CREATE_ROOM, data);
                        sendRequest(request);

                        String t;
                        do {
                            t = createRoomResponse;
                        } while (t == null);
                        return null;
                    }
                };
            }
        };

        service.start();
        service.setOnSucceeded(event -> {
            handleCreateRoomResponse();
        });
    }

    private void handleCreateRoomResponse() {

        if(createRoomResponse.charAt(1) == '0') {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText("Tạo phòng thành công");
            alert.setContentText("Mã phòng: " + createRoomResponse.substring(2));

            getRooms(root);

            Optional<ButtonType> result = alert.showAndWait();
        }

        else if (createRoomResponse.charAt(1) == '1'){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Thông báo");
            alert.setHeaderText("Tạo phòng lỗi");
            alert.showAndWait();

            Optional<ButtonType> result = alert.showAndWait();
            if(!result.isPresent() | result.get() == ButtonType.CANCEL){

            }
            else if(result.get() == ButtonType.OK){

            }
        }
    }

    public void bid(String price) {
        Service service = new Service() {
            @Override
            protected Task createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        Properties data = new Properties();
                        data.put("price", price);
                        data.put("userId", account.getUserId());
                        data.put("roomId", selectedRoom.getRoomId());

                        String request = RequestFactory.getRequest(RequestType.BID, data);
                        sendRequest(request);

                        String t;
                        do {
                            t = bidResponse;
                        } while (t == null);

                        return null;
                    }
                };
            }
        };

        service.start();

        service.setOnSucceeded(event -> {
            System.out.println(bidResponse);
        });
    }

    public void buy() {
        Service service = new Service() {
            @Override
            protected Task createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        Properties data = new Properties();
                        data.put("userId", account.getUserId());
                        data.put("roomId", selectedRoom.getRoomId());

                        String request = RequestFactory.getRequest(RequestType.BUY, data);
                        sendRequest(request);

                        String t;
                        do {
                            t = buyResponse;
                        } while (t == null);
                        return null;
                    }
                };
            }
        };

        service.start();

        service.setOnSucceeded(event -> {
            switch (bidResponse){
                case "50":
                    noticCallback.accept("Bạn đã đấu giá thành công.");
                    break;
                case "51":
                    noticCallback.accept("Bạn ra giá thấp hơn giá hiện tại của vật phẩm");
                case "52":
                    noticCallback.accept("Người tạo phòng không được tham gia đấu giá");
            }
        });
    }

    public void setLockBidCallback(Consumer<String> lockBid) {
        this.lockBidCallback = lockBid;
    }

    public void setNoticCallback(Consumer<String> noticCallback) {
        this.noticCallback = noticCallback;
    }
}
